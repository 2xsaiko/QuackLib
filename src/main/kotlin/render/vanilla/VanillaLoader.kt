package therealfarfetchd.quacklib.render.vanilla

import com.google.gson.*
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Mat4
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.core.modinterface.openResource
import therealfarfetchd.quacklib.api.render.ModelLoader
import therealfarfetchd.quacklib.core.init.ValidationContextImpl

/**
 * A model loader that loads vanilla (JSON) models/blockstates.
 * Format spec: https://gist.github.com/RainWarrior/0618131f51b8d37b80a6
 */
object VanillaLoader : ModelLoader {

  val rFullVariantSpec = Regex("^[A-Za-z_]+=[^,]+(,[A-Za-z_]+=[^,]+)*$")
  val rPartialVariantSpec = Regex("^[A-Za-z_]+$")
  val missingModel = ResourceLocation("minecraft:missing")

  override fun load(rl: ResourceLocation, block: BlockStateContainer, vc: ValidationContext) {
    val fixed = ResourceLocation(rl.namespace, "blockstates/${rl.path}.json")

    val filename = "${rl.path}.json"
    val content = openResource(fixed, true)
    if (content == null) {
      vc.error("Could not open block state '$rl' ('$fixed')")
      return //null
    }

    val je = try {
      content.reader().use(JsonParser()::parse)
    } catch (e: JsonParseException) {
      vc.error("Could not parse blockstate file '$rl': $e. More information in the log")
      logException(e)
      return //null
    }

    return scanRoot(je, filename, block, vc)
  }

  fun scanRoot(je: JsonElement, fname: String, block: BlockStateContainer, vc: ValidationContext) {
    val path = JsonPathSpec(fname)
    val jo = je.asJsonObjectOrNull

    if (jo == null) {
      vc.error(path, "Root object is not a valid JSON object!")
      return
    }

    val isForge = jo["forge_marker"].asIntOrNull == 1

    return if (isForge) {
      loadForgeModel(jo, path, block, vc)
    } else {
      loadVanillaModel(jo, path, block, vc)
    }
  }

  fun loadForgeModel(tree: JsonObject, path: JsonPathSpec, block: BlockStateContainer, vc: ValidationContext) {
    val defaults = loadVariant(tree["defaults"], path.subtree("defaults"), vc)
    val variants = loadVariants(tree["variants"], path.subtree("variants"), block, vc)
  }

  fun loadVanillaModel(tree: JsonObject, path: JsonPathSpec, block: BlockStateContainer, vc: ValidationContext) {
    vc.error(path, "Vanilla (non-forge) model loading not implemented!")
  }

  fun loadVariant(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext) {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!") // TODO this can also be an array
      return //null
    }

    val model = tree["model"]?.asStringOrNull?.let(::ResourceLocation)

    val textures = tree["textures"]?.let { loadTextures(it, path.subtree("textures"), vc) }.orEmpty()

    val transform = tree["transform"]?.let { loadTransformRoot(it, path.subtree("transform"), vc) }

    val x = tree["x"]?.asFloatOrNull?.also {
      if (it % 90f != 0f) vc.warn(path.subtree("x"), "Standard only supports rotations that are a multiple of 90°")
    }

    val y = tree["y"]?.asFloatOrNull?.also {
      if (it % 90f != 0f) vc.warn(path.subtree("y"), "Standard only supports rotations that are a multiple of 90°")
    }

    val z = tree["z"]?.asFloatOrNull?.also {
      vc.warn(path.subtree("z"), "Standard only supports 'x' and 'y' rotations.")
    }

    val uvlock = tree["uvlock"]?.asBoolOrNull

    val weight = tree["weight"]?.asIntOrNull

    // TODO submodel

    // TODO custom

  }

  private fun loadTransformRoot(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation = when {
    tree is JsonObject && TransformType.Values.any { tree.has(it.jname) } -> loadTransformMulti(tree, path, vc)
    else -> loadTransform(tree, path, vc)
  }

  private fun loadTransform(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation = when {
    tree is JsonPrimitive && tree.isString -> loadTransformFromFile(tree, path, vc)
    tree is JsonArray -> loadTransformMatrix(tree, path, vc)
    tree is JsonObject && tree.size() == 1 && tree.has("matrix") -> loadTransformMatrixPacked(tree, path, vc)
    tree is JsonObject && setOf("translation", "rotation", "scale", "post-rotation").any(tree::has) -> loadTransformTRSR(tree, path, vc)
    else -> {
      vc.error(path, "Could not detect transform type")
      IdentityTransformation
    }
  }

  private fun loadTransformMulti(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return IdentityTransformation
    }

    return MultiTransformation(
      tree.entrySet()
        .map { (k, v) -> TransformType.byJname[k].also { if (it == null) vc.error(path, "Invalid transform type '$k'") } to loadTransform(v, path.subtree(k), vc) }
        .mapNotNull { (k, v) -> if (k == null) null else k to v }
        .toMap()
    )
  }

  private fun loadTransformFromFile(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation {
    if (tree !is JsonPrimitive || !tree.isString) {
      vc.error(path, "Should be a string!")
      return IdentityTransformation
    }

    val rl = ResourceLocation(tree.asString)
    return loadTransformFromFile0(rl, path, vc)
  }

  private fun loadTransformFromFile0(rl: ResourceLocation, path: JsonPathSpec, vc: ValidationContext): Transformation {
    val fixed = ResourceLocation(rl.namespace, "transform/${rl.path}.json")
    val path1 = JsonPathSpec("${rl.path}.json")

    if (path == path1) {
      // TODO: better recursion fix
      // (tbh if you have recursion in your thing you kinda deserve it to crash, you fucking idiot)
      vc.error(path, "Transform refers to itself!")
    }

    val res = openResource(fixed, respectResourcePack = true) // do we want to respect resource pack? Why not

    if (res == null) {
      vc.error(path, "Could not open transform '$rl' ('$fixed')")
      return IdentityTransformation
    }

    val trtree = try {
      res.use { JsonParser().parse(it.reader()) }
    } catch (e: JsonParseException) {
      vc.error(path, "Could not parse transform file '$rl': $e. More information in the log")
      logException(e)
      return IdentityTransformation
    }

    return loadTransformRoot(trtree, path1, vc)
  }

  fun loadTransformFromResource(rl: ResourceLocation): Transformation? {
    val path = JsonPathSpec("<none>")
    val vc = ValidationContextImpl("Transformation $rl")
    val r = loadTransformFromFile0(rl, path, vc)
    if (!vc.isValid()) {
      vc.printMessages()
      return null
    }
    return r
  }

  private fun loadTransformMatrixPacked(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return IdentityTransformation
    }

    if (!tree.has("matrix")) {
      vc.error(path, "Must contain 'matrix' entry!")
    }

    return loadTransformMatrix(tree["matrix"], path.subtree("matrix"), vc)
  }

  private fun loadTransformMatrix(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation {
    if (tree !is JsonArray || tree.any { !it.isJsonArray }) {
      vc.error(path, "Should be a 2d array!")
      return IdentityTransformation
    }

    if (tree.size() != 3 || tree.any { it.asJsonArray.size() != 4 }) {
      vc.error(path, "Should be a 3x4 array!")
      return IdentityTransformation
    }

    val mat = tree.flatMap { it.asJsonArray.toList() }

    if (!mat.all { it is JsonPrimitive && it.isNumber }) {
      vc.error(path, "Should be an array of numbers!")
      return IdentityTransformation
    }

    val mat1 = mat.map { it.asNumber.toFloat() }

    return MatrixTransformation(Mat4(
      mat1[0], mat1[1], mat1[2], mat1[3],
      mat1[4], mat1[5], mat1[6], mat1[7],
      mat1[8], mat1[9], mat1[10], mat1[11],
      0f, 0f, 0f, 1f
    ))
  }

  private fun loadTransformTRSR(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Transformation {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return IdentityTransformation
    }

    val translation = tree["translation"]?.let { loadVec3(it, path.subtree("translation"), vc) }
                      ?: Vec3.Origin

    val rotation = tree["rotation"]?.let { loadRotation(it, path.subtree("rotation"), vc) }
                   ?: Mat4.Identity

    val scale = tree["scale"]?.let { loadScale(it, path.subtree("scale"), vc) }
                ?: Vec3(1, 1, 1)

    val postRotation = tree["post-rotation"]?.let { loadRotation(it, path.subtree("post-rotation"), vc) }
                       ?: Mat4.Identity

    return MatrixTransformation(Mat4
      .translate(translation)
      .times(rotation)
      .scale(scale.x, scale.y, scale.z)
      .times(postRotation)
    )
  }

  private fun loadVec3(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Vec3 {
    if (tree !is JsonArray) {
      vc.error(path, "Should be an array!")
      return Vec3.Origin
    }

    if (tree.size() != 3 || tree.any { it !is JsonPrimitive || !it.isNumber }) {
      vc.error(path, "Should contain exactly 3 numbers!")
      return Vec3.Origin
    }

    val (x, y, z) = tree.map { (it as JsonPrimitive).asFloat }

    return Vec3(x, y, z)
  }

  private fun loadRotation(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Mat4 {
    // rotation around an axis
    fun loadRotationO(tree: JsonObject): Mat4 {
      val (k, v) = tree.entrySet().first()
      if (k.length != 1 || k !in "xyz") {
        vc.error(path, "Expected an axis (x, y, z) as key")
        return Mat4.Identity
      }

      if (v !is JsonPrimitive || !v.isNumber) {
        vc.error(path, "Rotation must be a number")
        return Mat4.Identity
      }

      val x = if (k == "x") 1f else 0f
      val y = if (k == "y") 1f else 0f
      val z = if (k == "z") 1f else 0f
      return Mat4.rotate(x, y, z, v.asFloat)
    }

    if (tree is JsonObject && tree.size() == 1) {
      return loadRotationO(tree)
    }

    if (tree is JsonObject) {
      vc.error(path, "Multiple rotations in the same object not supported (objects are unordered)")
      vc.error(path, "Did you mean something like: ${tree.entrySet().joinToString(separator = ", ", prefix = "[", postfix = "]") { (k, v) -> "\"$k\": $v" }}")
      return Mat4.Identity
    }

    // quaternion
    if (tree is JsonArray && tree.size() == 4 && tree.all { it is JsonPrimitive && it.isNumber }) {
      val (x, y, z, w) = tree.map { (it as JsonPrimitive).asFloat }

      return Mat4(
        1 - 2 * y * y - 2 * z * z, 2 * x * y - 2 * z * w, 2 * x * z + 2 * y * w, 0f,
        2 * x * y + 2 * z * w, 1 - 2 * x * x - 2 * z * z, 2 * y * z - 2 * x * w, 0f,
        2 * x * z - 2 * y * w, 2 * y * z + 2 * x * w, 1 - 2 * x * x - 2 * y * y, 0f,
        0f, 0f, 0f, 1f
      )
    }

    // rotation chaining
    if (tree is JsonArray && tree.all { it is JsonObject && it.size() == 1 }) {
      return tree.fold(Mat4.Identity) { acc, a -> acc * loadRotationO(a.asJsonObject) }
    }

    vc.error(path, "Invalid rotation")
    return Mat4.Identity
  }

  private fun loadScale(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Vec3 {
    if (tree is JsonArray) {
      return loadVec3(tree, path, vc)
    }

    if (tree is JsonPrimitive && tree.isNumber) {
      val s = tree.asFloat
      return Vec3(s, s, s)
    }

    vc.error(path, "Should be a valid scale (vec3 or scalar)")
    return Vec3(1, 1, 1)
  }

  fun loadVariants(tree: JsonElement, path: JsonPathSpec, block: BlockStateContainer, vc: ValidationContext) {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return //emptySet()
    }

    val keys = tree.entrySet().map { it.key }
    val fullSpec = keys.filter(rFullVariantSpec::matches)
    val partSpec = (keys - fullSpec).filter(rPartialVariantSpec::matches)

    val malformed = keys - fullSpec - partSpec
    malformed.forEach {
      vc.error(path, "Malformed variant name '$it'")
    }

    val fVariants = fullSpec.associate { VariantFull(block, it, vc) to loadVariant(tree[it], path.subtree(it), vc) }
    val pVariants = partSpec.flatMap { loadVariantPart(tree[it], it, path.subtree(it), block, vc) }.toMap()
    val variants = fVariants + pVariants
  }
  //
  //  fun loadVariantPart0(tree: JsonElement, property: String, path: JsonPathSpec, block: BlockStateContainer, vc: ValidationContext): List<Pair<VariantPart<*>, Unit>> {
  //    val type = run{
  //
  //    }
  //  }

  @Suppress("UNCHECKED_CAST")
  fun loadVariantPart(tree: JsonElement, property: String, path: JsonPathSpec, block: BlockStateContainer, vc: ValidationContext): List<Pair<VariantPart<*>, Unit>> {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return emptyList()
    }

    val keys = tree.entrySet().map { it.key }
    val prop = block.getProperty(property)

    if (prop == null) {
      vc.error("Nonexistant property '$property' in block '${block.block.registryName}'!")
      return emptyList()
    }

    return keys.mapNotNull {
      val r = prop.parseValue(it)
      if (!r.isPresent) null
      else Pair(VariantPart(block, prop as IProperty<Comparable<Any>>, r.get() as Comparable<Any>), loadVariant(tree[it], path.subtree(it), vc))
    }
  }

  fun loadTextures(tree: JsonElement, path: JsonPathSpec, vc: ValidationContext): Map<String, ResourceLocation> {
    if (tree !is JsonObject) {
      vc.error(path, "Should be an object!")
      return emptyMap()
    }

    return tree.entrySet().mapNotNull { (k, v) ->
      val rl = v.asStringOrNull?.let(::ResourceLocation) ?: run {
        vc.error(path.subtree(k), "Value must be a string!")
        return@mapNotNull null
      }

      Pair(k, rl)
    }.toMap()
  }

  data class JsonPathSpec(val spec: String) {

    fun subarray(index: Int) = JsonPathSpec("$spec[$index]")

    fun subtree(key: String) = JsonPathSpec("$spec → $key")

    override fun toString(): String = spec

  }

  fun parseBlockState(sc: BlockStateContainer, stringSpec: String, vc: ValidationContext): IBlockState {
    return stringSpec.split(",").fold(sc.baseState) { acc, a ->
      val (key, value) = a.split("=", limit = 1)
      val prop = sc.getProperty(key)
      if (prop == null) {
        vc.error("Nonexistant property: '$key' (parsing blockstate specifier '$stringSpec' for block ${sc.block.registryName})")
        acc
      } else {
        val vp = prop.parseValue(value)
        if (vp.isPresent) {
          acc.withPropertyHax(prop, vp.get())
        } else {
          vc.error("Could not parse value '$value' for property '$key' (parsing blockstate specifier '$stringSpec' for block ${sc.block.registryName})")
          acc
        }
      }
    }
  }

  private fun ValidationContext.info(path: JsonPathSpec, msg: String) =
    info("(at $path) $msg")

  private fun ValidationContext.warn(path: JsonPathSpec, msg: String) =
    warn("(at $path) $msg")

  private fun ValidationContext.error(path: JsonPathSpec, msg: String) =
    error("(at $path) $msg")

  private val JsonElement.asJsonObjectOrNull: JsonObject?
    get() = this as? JsonObject

  private val JsonElement.asIntOrNull: Int?
    get() = if (this is JsonPrimitive && isNumber) asNumber.toInt() else null

  private val JsonElement.asFloatOrNull: Float?
    get() = if (this is JsonPrimitive && isNumber) asNumber.toFloat() else null

  private val JsonElement.asStringOrNull: String?
    get() = if (this is JsonPrimitive && !isNumber && !isBoolean) asString else null

  private val JsonElement.asBoolOrNull: Boolean?
    get() = if (this is JsonPrimitive && isBoolean) asBoolean else null

  // >_>
  @Suppress("UNCHECKED_CAST")
  private fun IBlockState.withPropertyHax(prop: IProperty<*>, value: Any): IBlockState =
    withProperty(prop as IProperty<Comparable<Any>>, value as Comparable<Any>)

}