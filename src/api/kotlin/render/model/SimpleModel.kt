package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Mat4
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture
import kotlin.reflect.KClass

@DslMarker
annotation class SimpleModelDSL

typealias ObjectBuilderProvider<T> = (SimpleModel.ModelContext) -> T

abstract class SimpleModel : Model {

  private val atlasTexs = mutableListOf<PreparedTexture>()

  override fun <T : DataSource<*>> accepts(type: KClass<T>): Boolean = true

  final override fun <T : DataSource<*>> getStaticRender(data: T, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    QuackLibAPI.impl.modelAPI.getStaticRender(this, data, getTexture)

  final override fun getUsedTextures(): List<ResourceLocation> =
    atlasTexs.map(PreparedTexture::resource)

  final override fun <T : DataSource<D>, D : DynDataSource> getDynamicRender(data: T, dyndata: D): List<Quad> {
    TODO("not implemented")
  }

  final override fun needsDynamicRender(): Boolean {
    TODO("not implemented")
  }

  fun useTexture(resource: String, addToAtlas: Boolean = true): PreparedTexture =
    useTexture(QuackLibAPI.impl.getResourceFromContext(resource), addToAtlas)

  fun useTexture(resource: ResourceLocation, addToAtlas: Boolean = true): PreparedTexture {
    val pt = PreparedTexture(resource, addToAtlas)
    if (addToAtlas) atlasTexs += pt
    return pt
  }

  fun <T> useRenderParam(): RenderParam<T> = RenderParam()

  abstract fun ModelContext.addObjects()

  @SimpleModelDSL
  interface ModelContext : ModelTemplates {

    val data: DataSource<*>

    val coordsScale: Float

    fun texture(pt: PreparedTexture): Texture

    fun addQuad(q: Quad)

    fun addQuads(q: List<Quad>) =
      q.forEach(::addQuad)

    fun <T : ModelConfigurationScope> add(prov: ObjectBuilderProvider<T>, op: T.() -> Unit)

    fun trPush()
    fun trPop()
    fun rotate(angle: Float, x: Float, y: Float, z: Float)
    fun translate(x: Float, y: Float, z: Float)
    fun scale(x: Float, y: Float, z: Float)
    fun scale(f: Float) = scale(f, f, f)
    fun transform(mat: Mat4)

    fun coordsScale(f: Float)
    fun coordsScale(i: Int) = coordsScale(i.toFloat())

    fun trNew(op: () -> Unit) {
      trPush()
      op()
      trPop()
    }

    fun dynamic(op: Dynamic.() -> Unit)

  }

  @SimpleModelDSL
  interface Dynamic : ModelContext {

    val dyndata: DynDataSource

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Can't have nested dynamic blocks!", level = DeprecationLevel.ERROR)
    override fun dynamic(op: Dynamic.() -> Unit) {
      error("Can't have nested dynamic blocks!")
    }

  }

  class PreparedTexture internal constructor(val resource: ResourceLocation, val isAtlasTex: Boolean)

  @Suppress("unused")
  inner class RenderParam<T> internal constructor() {
    private val rps = mutableMapOf<ResourceLocation, RenderProperty<*, T>>()

    fun setImpl(block: ResourceLocation, export: RenderProperty<*, T>) {
      rps[block] = export
    }

    fun getValue(block: BlockType, state: BlockRenderState): T {
      return state.getValue(rps.getValue(block.registryName))
    }
  }

}