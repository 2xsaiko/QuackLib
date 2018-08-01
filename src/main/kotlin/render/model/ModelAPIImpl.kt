package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.ModelAPI
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

object ModelAPIImpl : ModelAPI {

  override fun getStaticRender(model: SimpleModel, data: DataSource<*>, texture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val r = ModelContextImpl(data, texture)
    with(model) { r.addObjects() }
    return r.getQuads()
  }

}