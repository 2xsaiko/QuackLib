package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Mat4
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.*
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture

class ModelContextImpl(override val data: DataSource<*>, val getTexture: (ResourceLocation) -> AtlasTexture) : SimpleModel.ModelContext {

  override val Box: ObjectBuilderProvider<BoxConfigurationScope> = ::BoxConfigurationScopeImpl
  override val OBJ: ObjectBuilderProvider<ObjConfigurationScope> = ::ObjConfigurationScopeImpl

  override var coordsScale = 1f

  val quads = mutableListOf<Quad>()

  val transformStack = mutableListOf<Mat4>()
  var currentTransform = Mat4.Identity

  val dynops = mutableListOf<SimpleModel.Dynamic.() -> Unit>()

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
    dynops += op
  }

}