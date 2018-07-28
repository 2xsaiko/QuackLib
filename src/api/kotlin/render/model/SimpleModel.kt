package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture

abstract class SimpleModel : UniversalModel() {

  private val atlasTexs = mutableListOf<PreparedTexture>()

  override fun getStaticRender(data: DataSource, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val mc = ModelContextImpl(data, getTexture)
    mc.addObjects()
    return mc.getQuads()
  }

  fun useTexture(resource: String, addToAtlas: Boolean = true): PreparedTexture =
    useTexture(QuackLibAPI.impl.getResourceFromContext(resource), addToAtlas)

  fun useTexture(resource: ResourceLocation, addToAtlas: Boolean = true): PreparedTexture {
    val pt = PreparedTexture(resource, addToAtlas)
    if (addToAtlas) atlasTexs += pt
    return pt
  }

  override fun getUsedTextures(): List<ResourceLocation> =
    atlasTexs.map(PreparedTexture::resource)

  abstract fun ModelContext.addObjects()

  interface ModelContext {

    val data: DataSource

    fun texture(pt: PreparedTexture): Texture

  }

  class PreparedTexture internal constructor(val resource: ResourceLocation, internal val isAtlasTex: Boolean)

  private class ModelContextImpl(override val data: DataSource, val getTexture: (ResourceLocation) -> AtlasTexture) : ModelContext {

    internal fun getQuads(): List<Quad> {
      return emptyList()
    }

    override fun texture(pt: PreparedTexture): Texture {
      if (pt.isAtlasTex) {
        return getTexture(pt.resource)
      } else {
        TODO("getting non-atlas textures not implemented yet")
      }
    }
  }

}