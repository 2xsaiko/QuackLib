package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

interface ModelAPI {

  fun getStaticRender(model: SimpleModel, data: DataSource<*>, texture: (ResourceLocation) -> AtlasTexture): List<Quad>

}