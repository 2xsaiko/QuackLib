package therealfarfetchd.quacklib.render.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.PerspectiveMapWrapper
import net.minecraftforge.common.model.IModelState
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.toMatrix4f
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.render.vanilla.IdentityTransformation
import therealfarfetchd.quacklib.render.vanilla.Transformation
import therealfarfetchd.quacklib.render.vanilla.VanillaLoader
import therealfarfetchd.quacklib.render.vanilla.toTransformType
import java.util.*
import javax.vecmath.Matrix4f

class BakedModelBuilder private constructor() {

  lateinit var particleTexture: TextureAtlasSprite

  var builtinRenderer: Boolean = false

  var ambientOcclusion: Boolean = true

  var isGui3d: Boolean = true

  var overrides: ItemOverrideList = ItemOverrideList.NONE

  var transformation: Transformation = IdentityTransformation

  private val quads: MutableMap<EnumFacing?, MutableList<Quad>> = mutableMapOf()

  fun addQuad(q: Quad) {
    addQuad(q, getCullFace(q))
  }

  fun addQuad(q: Quad, cullface: EnumFacing?) {
    quads.computeIfAbsent(cullface) { mutableListOf() } += q
  }

  fun addQuads(p: Iterable<Quad>) {
    p.forEach(::addQuad)
  }

  private fun getCullFace(q: Quad): EnumFacing? {
    val f = q.facing
    val xyz = (f.directionVec.toVec3() + Vec3(1, 1, 1)) / 2
    val useX = f.axis == EnumFacing.Axis.X
    val useY = f.axis == EnumFacing.Axis.Y
    val useZ = f.axis == EnumFacing.Axis.Z

    if (
      !q.xyzComponents().all {
        (!useX || it.x == xyz.x) &&
        (!useY || it.y == xyz.y) &&
        (!useZ || it.z == xyz.z)
      }
    ) return null

    return f
  }

  @Suppress("OverridingDeprecatedMember")
  class BakedModel(
    private val particleTexture: TextureAtlasSprite,
    private val quads: Map<EnumFacing?, List<BakedQuad>>,
    private val builtinRenderer: Boolean,
    private val ambientOcclusion: Boolean,
    private val isGui3d: Boolean,
    private val overrides: ItemOverrideList,
    private val transformation: Transformation
  ) : IBakedModel {

    override fun getParticleTexture(): TextureAtlasSprite =
      particleTexture

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> =
      quads[side].orEmpty()

    override fun isBuiltInRenderer(): Boolean =
      builtinRenderer

    override fun isAmbientOcclusion(): Boolean =
      ambientOcclusion

    override fun isGui3d(): Boolean =
      isGui3d

    override fun getOverrides(): ItemOverrideList =
      overrides

    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> {
      return PerspectiveMapWrapper.handlePerspective(this, { Optional.ofNullable(TRSRTransformation(transformation.getTransformationMatrix(cameraTransformType.toTransformType())?.toMatrix4f())) }, cameraTransformType)
    }

  }

  companion object {
    val defaultBlock = VanillaLoader.loadTransformFromResource(ResourceLocation("forge", "default-block"))!!
    val defaultItem = VanillaLoader.loadTransformFromResource(ResourceLocation("forge", "default-item"))!!
    val defaultTool = VanillaLoader.loadTransformFromResource(ResourceLocation("forge", "default-tool"))!!

    operator fun invoke(state: IModelState, format: VertexFormat = DefaultVertexFormats.ITEM, op: BakedModelBuilder.() -> Unit): IBakedModel {
      val builder = BakedModelBuilder().also(op)
      if (!builder::particleTexture.isInitialized) error("Particle texture not set!")
      return BakedModel(
        builder.particleTexture,
        builder.quads.mapValues { (_, a) -> a.map { it.bake(format) } },
        builder.builtinRenderer,
        builder.ambientOcclusion,
        builder.isGui3d,
        builder.overrides,
        builder.transformation
      )
    }

    // shitty debug code, just in case I ever need it again

    // fun dumpTransforms(m: Map<ItemCameraTransforms.TransformType, TRSRTransformation>) {
    //   println("{")
    //   for ((type, tr) in m) {
    //     val n = TransformType.valueOf(type.name).jname
    //     println("\"$n\": {")
    //     println("\"translation\"  :[${tr.translation.toString().drop(1).dropLast(1)}],")
    //     println("\"rotation\"     :[${tr.leftRot.toString().drop(1).dropLast(1)}],")
    //     println("\"scale\"        :[${tr.scale.toString().drop(1).dropLast(1)}],")
    //     println("\"post-rotation\":[${tr.rightRot.toString().drop(1).dropLast(1)}]")
    //     println("},")
    //   }
    //   println("}")
    // }
    //
    // fun dumpTransforms(t: Transformation) {
    //   val r = TransformType.Values.map {
    //     val m = t.getTransformationMatrix(it) ?: Mat4.Identity
    //     val trsr = TRSRTransformation(m.toMatrix4f())
    //     val tt1 = ItemCameraTransforms.TransformType.valueOf(it.name)
    //     tt1 to trsr
    //   }.toMap()
    //   dumpTransforms(r)
    // }
    //
    // fun dumpTransforms(ict: ItemCameraTransforms) {
    //   val l = mapOf(
    //     ItemCameraTransforms.TransformType.GUI to ict.gui,
    //     ItemCameraTransforms.TransformType.GROUND to ict.ground,
    //     ItemCameraTransforms.TransformType.HEAD to ict.head,
    //     ItemCameraTransforms.TransformType.FIXED to ict.fixed,
    //     ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND to ict.thirdperson_left,
    //     ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND to ict.thirdperson_right,
    //     ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND to ict.firstperson_left,
    //     ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND to ict.firstperson_right
    //   ).mapValues { (_, it) -> TRSRTransformation(it) }
    //   dumpTransforms(l)
    // }
    //
    // private fun makeQuaternion(p_188035_0_: Float, p_188035_1_: Float, p_188035_2_: Float): Quaternion {
    //   val f = p_188035_0_ * 0.017453292f
    //   val f1 = p_188035_1_ * 0.017453292f
    //   val f2 = p_188035_2_ * 0.017453292f
    //   val f3 = MathHelper.sin(0.5f * f)
    //   val f4 = MathHelper.cos(0.5f * f)
    //   val f5 = MathHelper.sin(0.5f * f1)
    //   val f6 = MathHelper.cos(0.5f * f1)
    //   val f7 = MathHelper.sin(0.5f * f2)
    //   val f8 = MathHelper.cos(0.5f * f2)
    //   return Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7, f4 * f6 * f8 - f3 * f5 * f7)
    // }
  }

}