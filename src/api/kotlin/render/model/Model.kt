package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import kotlin.reflect.KClass

interface Model {

  fun <T : DataSource<*>> accepts(type: KClass<T>): Boolean

  fun <T : DataSource<*>> getStaticRender(data: T, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>

  fun getUsedTextures(): List<ResourceLocation>

  // TODO: ugh
  fun isItemTransformation(): Boolean = false

  fun getParticleTexture(getTexture: (ResourceLocation) -> AtlasTexture): AtlasTexture

  fun <T : DataSource<D>, D : DynDataSource> getDynamicRender(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> = emptyList()

  fun <T : DataSource<D>, D : DynDataSource> renderGl(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture) {}

  fun needsDynamicRender(): Boolean = false

  fun needsGlRender(): Boolean = false

}