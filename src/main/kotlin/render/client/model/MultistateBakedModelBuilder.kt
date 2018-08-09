//package therealfarfetchd.quacklib.render.client.model
//
//import net.minecraft.block.state.IBlockState
//import net.minecraft.client.renderer.block.model.IBakedModel
//import net.minecraft.client.renderer.block.model.ItemCameraTransforms
//import net.minecraft.client.renderer.block.model.ItemOverrideList
//import net.minecraft.client.renderer.texture.TextureAtlasSprite
//import net.minecraft.client.renderer.vertex.DefaultVertexFormats
//import net.minecraft.client.renderer.vertex.VertexFormat
//import net.minecraft.util.EnumFacing
//import net.minecraftforge.client.model.PerspectiveMapWrapper
//import net.minecraftforge.common.model.TRSRTransformation
//import org.apache.commons.lang3.tuple.Pair
//import therealfarfetchd.math.Vec3
//import therealfarfetchd.quacklib.api.core.extensions.toMatrix4f
//import therealfarfetchd.quacklib.api.core.extensions.toVec3
//import therealfarfetchd.quacklib.api.render.Quad
//import therealfarfetchd.quacklib.render.vanilla.IdentityTransformation
//import therealfarfetchd.quacklib.render.vanilla.Transformation
//import therealfarfetchd.quacklib.render.vanilla.toTransformType
//import java.util.*
//import javax.vecmath.Matrix4f
//
//class MultistateBakedModelBuilder private constructor() {
//
//  lateinit var particleTexture: TextureAtlasSprite
//
//  lateinit var fallbackModel: IBakedModel
//
//  var builtinRenderer: Boolean = false
//
//  var ambientOcclusion: Boolean = true
//
//  var isGui3d: Boolean = true
//
//  var overrides: ItemOverrideList = ItemOverrideList.NONE
//
//  var transformation: Transformation = IdentityTransformation
//
//  private var genOp: List<QuadGenContext.() -> Unit> = emptyList()
//
//  fun data(op: QuadGenContext.() -> Unit) {
//    genOp += op
//  }
//
//  @Suppress("OverridingDeprecatedMember")
//  class MultistateModel(
//    private val vertexFormat: VertexFormat,
//    private val particleTexture: TextureAtlasSprite,
//    private val genOp: QuadGenContext.() -> Unit,
//    private val fallbackModel: IBakedModel,
//    private val builtinRenderer: Boolean,
//    private val ambientOcclusion: Boolean,
//    private val isGui3d: Boolean,
//    private val overrides: ItemOverrideList,
//    private val transformation: Transformation
//  ) : MultistateBakedModel() {
//
//    private val perspectives = ItemCameraTransforms.TransformType.values().associate { tt ->
//      tt to PerspectiveMapWrapper.handlePerspective(this, { Optional.ofNullable(TRSRTransformation(transformation.getTransformationMatrix(tt.toTransformType())?.toMatrix4f())) }, tt)
//    }
//
//    override fun getParticleTexture(): TextureAtlasSprite =
//      particleTexture
//
//    override fun createModel(state: IBlockState?): IBakedModel {
//      return try {
//        val qgc = QuadGenContext(state).also(genOp)
//        BakedModelBuilder(vertexFormat) {
//          particleTexture = this@MultistateModel.particleTexture
//          qgc.getQuads().forEach { (cull, quads) -> addQuads(quads, cull) }
//        }
//      } catch (e: Exception) {
//        fallbackModel
//      }
//    }
//
//    override fun isBuiltInRenderer(): Boolean =
//      builtinRenderer
//
//    override fun isAmbientOcclusion(): Boolean =
//      ambientOcclusion
//
//    override fun isGui3d(): Boolean =
//      isGui3d
//
//    override fun getOverrides(): ItemOverrideList =
//      overrides
//
//    override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> =
//      perspectives.getValue(cameraTransformType)
//
//  }
//
//  class QuadGenContext(val state: IBlockState?) {
//
//    private val quads: MutableMap<EnumFacing?, MutableList<Quad>> = mutableMapOf()
//
//    fun getQuads(): Map<EnumFacing?, MutableList<Quad>> = quads
//
//    fun addQuad(q: Quad) {
//      addQuad(q, getCullFace(q))
//    }
//
//    fun addQuad(q: Quad, cullface: EnumFacing?) {
//      quads.computeIfAbsent(cullface) { mutableListOf() } += q
//    }
//
//    fun addQuads(p: Iterable<Quad>) {
//      p.forEach(::addQuad)
//    }
//
//    private fun getCullFace(q: Quad): EnumFacing? {
//      val f = q.facing
//      val xyz = (f.directionVec.toVec3() + Vec3(1, 1, 1)) / 2
//      val useX = f.axis == EnumFacing.Axis.X
//      val useY = f.axis == EnumFacing.Axis.Y
//      val useZ = f.axis == EnumFacing.Axis.Z
//
//      if (
//        !q.xyzComponents().all {
//          (!useX || it.x == xyz.x) &&
//          (!useY || it.y == xyz.y) &&
//          (!useZ || it.z == xyz.z)
//        }
//      ) return null
//
//      return f
//    }
//  }
//
//  companion object {
//
//    operator fun invoke(format: VertexFormat = DefaultVertexFormats.ITEM, op: MultistateBakedModelBuilder.() -> Unit): IBakedModel {
//      val builder = MultistateBakedModelBuilder().also(op)
//      if (!builder::particleTexture.isInitialized) error("Particle texture not set!")
//      return MultistateModel(
//        format,
//        builder.particleTexture,
//        { builder.genOp.forEach { it(this) } },
//        builder.fallbackModel,
//        builder.builtinRenderer,
//        builder.ambientOcclusion,
//        builder.isGui3d,
//        builder.overrides,
//        builder.transformation
//      )
//    }
//
//  }
//
//}