package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.ObjConfigurationScope
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

class ObjConfigurationScopeImpl(val ctx: SimpleModel.ModelContext) : ObjConfigurationScope {

  override fun getQuads(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    TODO("not implemented")
  }

  override fun source(resource: ResourceLocation) {
    TODO("not implemented")
  }

  override fun source(resource: String) {
    TODO("not implemented")
  }

  override fun texture(t: SimpleModel.PreparedTexture?, material0: String) {
    TODO("not implemented")
  }

}