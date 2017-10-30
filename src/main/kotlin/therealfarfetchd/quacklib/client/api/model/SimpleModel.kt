package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.client.api.render.QuadFactory
import therealfarfetchd.quacklib.client.api.render.wires.TransformRules
import therealfarfetchd.quacklib.common.api.extensions.compose
import therealfarfetchd.quacklib.common.api.extensions.mapIf
import therealfarfetchd.quacklib.common.api.extensions.mapWithCopy
import therealfarfetchd.quacklib.common.api.util.StringPackedProps
import therealfarfetchd.quacklib.common.api.util.Vec2
import therealfarfetchd.quacklib.common.api.util.Vec3

abstract class SimpleModel : IModel {
  protected val mc = Minecraft.getMinecraft()

  override fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad> {
    val builder = ModelBuilder(face)
    addShapes(state, builder)
    return filterCategories(face, builder.quads).map(Quad::bake)
  }

  override fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad> {
    val builder = ModelBuilder(face)
    addShapes(stack, builder)
    return filterCategories(face, builder.quads).map(Quad::bake)
  }

  private fun filterCategories(face: EnumFacing?, quads: List<Quad>): List<Quad> {
    return quads.mapWithCopy { q ->
      when {
        q.facing == EnumFacing.UP && q.vert1.y == 1f && q.vert2.y == 1f && q.vert3.y == 1f && q.vert4.y == 1f -> EnumFacing.UP
        q.facing == EnumFacing.DOWN && q.vert1.y == 0f && q.vert2.y == 0f && q.vert3.y == 0f && q.vert4.y == 0f -> EnumFacing.DOWN
        q.facing == EnumFacing.NORTH && q.vert1.z == 0f && q.vert2.z == 0f && q.vert3.z == 0f && q.vert4.z == 0f -> EnumFacing.NORTH
        q.facing == EnumFacing.SOUTH && q.vert1.z == 1f && q.vert2.z == 1f && q.vert3.z == 1f && q.vert4.z == 1f -> EnumFacing.SOUTH
        q.facing == EnumFacing.WEST && q.vert1.x == 0f && q.vert2.x == 0f && q.vert3.x == 0f && q.vert4.x == 0f -> EnumFacing.WEST
        q.facing == EnumFacing.EAST && q.vert1.x == 1f && q.vert2.x == 1f && q.vert3.x == 1f && q.vert4.x == 1f -> EnumFacing.EAST
        else -> null
      }
    }.filter { it.second == face }.map { it.first }
  }

  abstract fun addShapes(state: IExtendedBlockState, model: ModelBuilder)
  abstract fun addShapes(stack: ItemStack, model: ModelBuilder)

  protected fun vec(x: Double, y: Double) = Vec2(x.toFloat(), y.toFloat())
  protected fun vec(x: Float, y: Float) = Vec2(x, y)
  protected fun vec16(x: Double, y: Double) = Vec2(x.toFloat() / 16F, y.toFloat() / 16F)
  protected fun vec16(x: Float, y: Float) = Vec2(x / 16F, y / 16F)
  protected fun vec16(x: Int, y: Int) = Vec2(x.toFloat() / 16F, y.toFloat() / 16F)

  protected fun vec(x: Double, y: Double, z: Double) = Vec3(x.toFloat(), y.toFloat(), z.toFloat())
  protected fun vec(x: Float, y: Float, z: Float) = Vec3(x, y, z)
  protected fun vec16(x: Double, y: Double, z: Double) = Vec3(x.toFloat() / 16F, y.toFloat() / 16F, z.toFloat() / 16F)
  protected fun vec16(x: Float, y: Float, z: Float) = Vec3(x / 16F, y / 16F, z / 16F)
  protected fun vec16(x: Int, y: Int, z: Int) = Vec3(x.toFloat() / 16F, y.toFloat() / 16F, z.toFloat() / 16F)

  protected fun texture(tex: TextureAtlasSprite): TextureTemplate = TextureTemplate(tex, Vec2(0f, 0f), Vec2(1f, 1f), auto = true)

  protected fun texture(tex: TextureAtlasSprite, uv: Vec2, width: Float, height: Float, flip: Boolean = false) =
    TextureTemplate(tex, uv, uv + Vec2(width, height), flip = flip)

  protected fun texture(tex: TextureAtlasSprite, u: Float, v: Float, u1: Float, v1: Float, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u, v), Vec2(u1, v1), flip = flip)

  protected fun texture(tex: TextureAtlasSprite, u: Double, v: Double, u1: Double, v1: Double, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()), Vec2(u1.toFloat(), v1.toFloat()), flip = flip)

  protected fun texture(tex: TextureAtlasSprite, u: Int, v: Int, u1: Int, v1: Int, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()), Vec2(u1.toFloat(), v1.toFloat()), flip = flip)

  protected fun texture(size: Int, tex: TextureAtlasSprite, u: Double, v: Double, u1: Double, v1: Double, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()) / size, Vec2(u1.toFloat(), v1.toFloat()) / size, flip = flip)

  protected fun texture(size: Int, tex: TextureAtlasSprite, u: Int, v: Int, u1: Int, v1: Int, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()) / size, Vec2(u1.toFloat(), v1.toFloat()) / size, flip = flip)

  protected fun texture(size: Int, tex: TextureAtlasSprite, uv: Vec2, width: Float, height: Float, flip: Boolean = false) =
    TextureTemplate(tex, uv / size, (uv + Vec2(width, height)) / size, flip = flip)

  protected fun texture16(tex: TextureAtlasSprite, uv: Vec2, width: Float, height: Float, flip: Boolean = false) =
    TextureTemplate(tex, uv / 16f, (uv + Vec2(width, height)) / 16f, flip = flip)

  protected fun texture16(tex: TextureAtlasSprite, u: Float, v: Float, u1: Float, v1: Float, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u, v) / 16f, Vec2(u1, v1) / 16f, flip = flip)

  protected fun texture16(tex: TextureAtlasSprite, u: Double, v: Double, u1: Double, v1: Double, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()) / 16f, Vec2(u1.toFloat(), v1.toFloat()) / 16f, flip = flip)

  protected fun texture16(tex: TextureAtlasSprite, u: Int, v: Int, u1: Int, v1: Int, flip: Boolean = false) =
    TextureTemplate(tex, Vec2(u.toFloat(), v.toFloat()) / 16f, Vec2(u1.toFloat(), v1.toFloat()) / 16f, flip = flip)

  protected val missingTex: TextureAtlasSprite
    get() = Minecraft.getMinecraft().textureMapBlocks.missingSprite
}

interface IQuadFactory {
  fun createQuads(facing: EnumFacing?): List<Quad>
}

class ModelBuilder(val face: EnumFacing?) {
  private val context = Context(this)

  private val identity: (Quad) -> Quad = { it }
  private var trStack: List<(Quad) -> Quad> = listOf(identity)

  var quads: List<Quad> = emptyList(); private set

  operator fun invoke(op: ModelBuilder.Context.() -> Unit) = with(context, op)

  class Context(private val builder: ModelBuilder) {
    fun box(op: BoxTemplate.() -> Unit) {
      val t = BoxTemplate().also(op)
      builder.quads += t.createQuads(builder.face).map(builder.trStack.last())
    }

    fun face(op: FaceTemplate.() -> Unit) {
      val t = FaceTemplate().also(op)
      builder.quads += t.createQuads(builder.face).map(builder.trStack.last())
    }

    fun identity() {
      builder.trStack = listOf(builder.identity)
    }

    fun trPush() {
      builder.trStack += builder.trStack.last()
    }

    fun trPop() {
      if (builder.trStack.size <= 1) error("Stack underflow")
      builder.trStack = builder.trStack.dropLast(1)
    }

    fun transform(op: Quad.() -> Quad) {
      val l = builder.trStack.last()
      builder.trStack = builder.trStack.dropLast(1) + (l compose op)
    }
  }
}

class BoxTemplate : IQuadFactory {
  var min = Vec3(0F, 0F, 0F)
  var max = Vec3(1F, 1F, 1F)

  /**
   * Render the texture on both sides (inside the box and outside).
   */
  var cull = true

  /**
   * Render the texture on the inside of the box.
   */
  var inverted = false

  var transform: String = ""
    set(value) {
      field = value
      transformOp = TransformRules.parseCmd(StringPackedProps(transform))
    }

  var transformOp: (Quad) -> Quad = { it }

  var up: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.x, max.x), minOf(min.z, max.z)), Vec2(maxOf(min.x, max.x), maxOf(min.z, max.z)), postProc = value.postProc)
    }

  var down: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.x, max.x), minOf(min.z, max.z)), Vec2(maxOf(min.x, max.x), maxOf(min.z, max.z)), postProc = value.postProc)
    }

  var north: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.x, max.x), 1 - maxOf(min.y, max.y)), Vec2(maxOf(min.x, max.x), 1 - minOf(min.y, max.y)), postProc = value.postProc)
    }

  var south: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.x, max.x), 1 - maxOf(min.y, max.y)), Vec2(maxOf(min.x, max.x), 1 - minOf(min.y, max.y)), postProc = value.postProc)
    }

  var west: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.z, max.z), 1 - maxOf(min.y, max.y)), Vec2(maxOf(min.z, max.z), 1 - minOf(min.y, max.y)), postProc = value.postProc)
    }

  var east: TextureTemplate? = null
    set(value) {
      field = if (value == null || !value.auto) value
      else TextureTemplate(value.texture, Vec2(minOf(min.z, max.z), 1 - maxOf(min.y, max.y)), Vec2(maxOf(min.z, max.z), 1 - minOf(min.y, max.y)), postProc = value.postProc)
    }

  var all: TextureTemplate? = null
    set(value) {
      down = value
      up = value
      north = value
      south = value
      east = value
      west = value
      field = value
    }

  override fun createQuads(facing: EnumFacing?): List<Quad> {
    val minx = minOf(min.x, max.x)
    val miny = minOf(min.y, max.y)
    val minz = minOf(min.z, max.z)
    val maxx = maxOf(min.x, max.x)
    val maxy = maxOf(min.y, max.y)
    val maxz = maxOf(min.z, max.z)

    var quads: List<Quad> = emptyList()

    up?.also {
      quads += QuadFactory.makeQuad(minx, maxy, minz, maxx, maxy, maxz, EnumFacing.UP, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }
    down?.also {
      quads += QuadFactory.makeQuad(minx, miny, minz, maxx, miny, maxz, EnumFacing.DOWN, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .flipTexturedSide
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }
    north?.also {
      quads += QuadFactory.makeQuad(minx, maxy, minz, maxx, miny, minz, EnumFacing.NORTH, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .flipTexturedSide
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }
    south?.also {
      quads += QuadFactory.makeQuad(minx, maxy, maxz, maxx, miny, maxz, EnumFacing.SOUTH, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }
    west?.also {
      quads += QuadFactory.makeQuad(minx, maxy, minz, minx, miny, maxz, EnumFacing.WEST, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }
    east?.also {
      quads += QuadFactory.makeQuad(maxx, maxy, minz, maxx, miny, maxz, EnumFacing.EAST, it.uv.x, it.uv.y, it.uv1.x, it.uv1.y, it.texture)
        .flipTexturedSide
        .mapIf(it.flip, Quad::rotatedTexture90)
        .let(it.postProc)
    }

    if (inverted && cull) {
      quads = quads.map(Quad::flipTexturedSide)
    }

    if (!cull) {
      quads += quads.map(Quad::flipTexturedSide)
    }

    return quads.map(transformOp)
  }
}

class FaceTemplate : IQuadFactory {
  var min = Vec2(0f, 0f)
  var max = Vec2(0f, 0f)
  var depth = 0f
  var facing = EnumFacing.UP

  override fun createQuads(facing: EnumFacing?): List<Quad> {
    TODO("not implemented")
  }
}

data class TextureTemplate(
  val texture: TextureAtlasSprite,
  val uv: Vec2,
  val uv1: Vec2,
  val flip: Boolean = false,
  val auto: Boolean = false,
  val postProc: Quad.() -> Quad = { this }
)