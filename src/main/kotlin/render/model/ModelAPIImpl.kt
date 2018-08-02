package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.DynDataSource
import therealfarfetchd.quacklib.api.render.model.ModelAPI
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

object ModelAPIImpl : ModelAPI {

  override fun getStaticRender(model: SimpleModel, data: DataSource<*>, texture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val r = ModelContextImpl(data, texture)
    with(model) { r.addObjects() }
    return r.getQuads()
  }

  override fun <T : DynDataSource> getDynamicRender(model: SimpleModel, data: DataSource<T>, dyndata: T, texture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val r = ModelContextImpl(data, texture) // TODO replace with faster context that doesn't evaluate all the static models
    with(model) { r.addObjects() }
    return r.dynops.flatMap {
      val dyn = ModelContextImpl.Dynamic(it, data, dyndata, texture)
      it.op(dyn)
      dyn.getQuads()
    }
  }

}