package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Mat4
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.item.render.ItemRenderState
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture
import kotlin.reflect.KClass

@DslMarker
annotation class SimpleModelDSL

typealias ObjectBuilderProvider<T> = (SimpleModel.ModelContext) -> T

abstract class SimpleModel(val useDynamic: Boolean = false, val useGL: Boolean = false) : Model {

  private val atlasTexs = mutableListOf<PreparedTexture>()

  override fun <T : DataSource<*>> accepts(type: KClass<T>): Boolean = true

  final override fun <T : DataSource<*>> getStaticRender(data: T, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    QuackLibAPI.impl.modelAPI.getStaticRender(this, data, getTexture)

  final override fun getUsedTextures(): List<ResourceLocation> =
    atlasTexs.map(PreparedTexture::resource)

  final override fun <T : DataSource<D>, D : DynDataSource> getDynamicRender(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    QuackLibAPI.impl.modelAPI.getDynamicRender(this, data, dyndata, getTexture)

  override fun <T : DataSource<D>, D : DynDataSource> renderGl(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture) =
    QuackLibAPI.impl.modelAPI.renderGl(this, data, dyndata, getTexture)

  final override fun needsDynamicRender(): Boolean = useDynamic

  final override fun needsGlRender(): Boolean = useGL

  fun useTexture(resource: String, addToAtlas: Boolean = true): PreparedTexture =
    useTexture(QuackLibAPI.impl.getResourceFromContext(resource), addToAtlas)

  fun useTexture(resource: ResourceLocation, addToAtlas: Boolean = true): PreparedTexture {
    val pt = PreparedTexture(resource, addToAtlas)
    if (addToAtlas) atlasTexs += pt
    return pt
  }

  fun <T> useRenderParam(): RenderParam<T> = RenderParam()

  final override fun getParticleTexture(getTexture: (ResourceLocation) -> AtlasTexture): AtlasTexture {
    return if (getParticleTexture().isAtlasTex) {
      getTexture(getParticleTexture().resource)
    } else {
      getTexture(ResourceLocation("quacklib", "error"))
    }
  }

  abstract fun getParticleTexture(): PreparedTexture

  abstract fun ModelContext.addObjects()

  @SimpleModelDSL
  interface BaseContext {

    val data: DataSource<*>

  }

  @SimpleModelDSL
  interface ModelContextBase : BaseContext, ModelTemplates {

    val coordsScale: Float

    fun texture(pt: PreparedTexture): Texture

    fun addQuad(q: Quad)

    fun addQuads(q: Iterable<Quad>) =
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

  }

  interface ModelContext : ModelContextBase {

    fun dynamic(op: Dynamic.() -> Unit)

    fun gl(op: GlContext.() -> Unit)

  }

  @SimpleModelDSL
  interface Dynamic : ModelContextBase {

    val dyndata: DynDataSource

  }

  interface GlContext : BaseContext {

    val dyndata: DynDataSource

  }

  class PreparedTexture internal constructor(val resource: ResourceLocation, val isAtlasTex: Boolean)

  @Suppress("unused")
  inner class RenderParam<T> internal constructor() {
    private val rpbs = mutableMapOf<ResourceLocation, RenderProperty<*, Block, T>>()
    private val rpis = mutableMapOf<ResourceLocation, RenderProperty<*, Item, T>>()

    fun setImplBlock(block: ResourceLocation, export: RenderProperty<*, Block, T>) {
      rpbs[block] = export
    }

    fun getValueBlock(block: BlockType, state: BlockRenderState): T {
      return state.getValue(rpbs.getValue(block.registryName))
    }

    fun setImplItem(item: ResourceLocation, export: RenderProperty<*, Item, T>) {
      rpis[item] = export
    }

    fun getValueItem(item: ItemType, state: ItemRenderState): T {
      return state.getValue(rpis.getValue(item.registryName))
    }
  }

}