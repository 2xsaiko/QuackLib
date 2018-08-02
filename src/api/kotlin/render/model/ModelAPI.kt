package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

interface ModelAPI {

  fun getStaticRender(model: SimpleModel, data: DataSource<*>, texture: (ResourceLocation) -> AtlasTexture): List<Quad>

  fun <T : DynDataSource> getDynamicRender(model: SimpleModel, data: DataSource<T>, dyndata: T, texture: (ResourceLocation) -> AtlasTexture): List<Quad>

}