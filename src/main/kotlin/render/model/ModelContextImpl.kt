package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Mat4
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.*
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture

open class ModelContextImpl(
  override val data: DataSource<*>,
  val getTexture: (ResourceLocation) -> AtlasTexture,
  val allowDyn: Boolean,
  val allowGl: Boolean
) : SimpleModel.ModelContext {

  override val Box: ObjectBuilderProvider<BoxConfigurationScope> = ::BoxConfigurationScopeImpl
  override val OBJ: ObjectBuilderProvider<ObjConfigurationScope> = ::ObjConfigurationScopeImpl

  override var coordsScale = 1f

  val quads = mutableListOf<Quad>()

  val transformStack = mutableListOf<Mat4>()
  var currentTransform = Mat4.Identity

  val dynops = mutableListOf<DynState>()
  val glops = mutableListOf<GlState>()

  internal fun getQuads(): List<Quad> {
    return quads
  }

  override fun texture(pt: SimpleModel.PreparedTexture): Texture {
    if (pt.isAtlasTex) {
      return getTexture(pt.resource)
    } else {
      TODO("getting non-atlas textures not implemented yet")
    }
  }

  override fun addQuad(q: Quad) {
    quads += q.transform(currentTransform)
  }

  override fun <T : ModelConfigurationScope> add(prov: ObjectBuilderProvider<T>, op: T.() -> Unit) {
    addQuads(prov(this).also(op).getQuads(getTexture))
  }

  override fun trPush() {
    transformStack += currentTransform
  }

  override fun trPop() {
    currentTransform = transformStack.removeAt(transformStack.size - 1)
  }

  override fun rotate(angle: Float, x: Float, y: Float, z: Float) {
    transform(Mat4.rotate(x, y, z, angle))
  }

  override fun translate(x: Float, y: Float, z: Float) {
    transform(Mat4.translate(x / coordsScale, y / coordsScale, z / coordsScale))
  }

  override fun scale(x: Float, y: Float, z: Float) {
    transform(Mat4.scale(x, y, z))
  }

  override fun transform(mat: Mat4) {
    currentTransform *= mat
  }

  override fun coordsScale(f: Float) {
    coordsScale = f
  }

  override fun dynamic(op: SimpleModel.Dynamic.() -> Unit) {
    if (!allowDyn) {
      // FIXME this should be a validation error
      error("Can't use dynamic objects in this model! Try setting useDynamic to true")
    }
    dynops += DynState(transformStack + currentTransform, coordsScale, op)
  }

  override fun gl(op: SimpleModel.GlContext.() -> Unit) {
    if (!allowGl) {
      // FIXME this should be a validation error
      error("Can't use GL drawing in this model! Try setting useGL to true")
    }
    glops += GlState(op)
  }

  data class DynState(val trStack: List<Mat4>, val cscale: Float, val op: SimpleModel.Dynamic.() -> Unit)

  data class GlState(val op: SimpleModel.GlContext.() -> Unit)

  class DynWrapper(
    data: DataSource<*>,
    getTexture: (ResourceLocation) -> AtlasTexture,
    allowDyn: Boolean,
    allowGl: Boolean) : ModelContextImpl(data, getTexture, allowDyn, allowGl) {

    override fun addQuad(q: Quad) {
      // No-op
    }

    override fun <T : ModelConfigurationScope> add(prov: ObjectBuilderProvider<T>, op: T.() -> Unit) {
      // No-op
    }

  }

  class Dynamic(
    state: DynState,
    data: DataSource<*>,
    override val dyndata: DynDataSource,
    getTexture: (ResourceLocation) -> AtlasTexture
  ) : ModelContextImpl(data, getTexture, true, true), SimpleModel.Dynamic {

    init {
      transformStack += state.trStack.dropLast(1)
      currentTransform = state.trStack.last()
      coordsScale = state.cscale
    }

    override fun dynamic(op: SimpleModel.Dynamic.() -> Unit): Unit =
      error("Can't have nested dynamic blocks!")
  }

  class GlContext(override val data: DataSource<*>, override val dyndata: DynDataSource) : SimpleModel.GlContext

}