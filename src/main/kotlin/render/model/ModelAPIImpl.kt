package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.DynDataSource
import therealfarfetchd.quacklib.api.render.model.ModelAPI
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.model.obj.OBJRoot
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.render.model.objloader.OBJModelProvider

object ModelAPIImpl : ModelAPI {

  override fun getStaticRender(model: SimpleModel, data: DataSource<*>, texture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val r = ModelContextImpl(data, texture, model.useDynamic, model.useGL)
    with(model) { r.addObjects() }
    return r.getQuads()
  }

  override fun <T : DynDataSource> getDynamicRender(model: SimpleModel, data: DataSource<T>, dyndata: T, texture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    // TODO cache????
    val r = ModelContextImpl.DynWrapper(data, texture, model.useDynamic, model.useGL)
    with(model) { r.addObjects() }
    return r.dynops.flatMap {
      val dyn = ModelContextImpl.Dynamic(it, data, dyndata, texture)
      it.op(dyn)
      dyn.getQuads()
    }
  }

  override fun <T : DynDataSource> renderGl(model: SimpleModel, data: DataSource<T>, dyndata: T, texture: (ResourceLocation) -> AtlasTexture) {
    // TODO cache????
    val r = ModelContextImpl.DynWrapper(data, texture, model.useDynamic, model.useGL)
    with(model) { r.addObjects() }
    for (state in r.glops) {
      val ctx = ModelContextImpl.GlContext(data, dyndata)
      state.op(ctx)
    }
  }

  override fun loadOBJ(rl: ResourceLocation): OBJRoot? =
    OBJModelProvider.load(rl)

}