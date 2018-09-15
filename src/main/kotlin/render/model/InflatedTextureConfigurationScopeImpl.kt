package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.InflatedTextureConfigurationScope
import therealfarfetchd.quacklib.api.render.model.InflatedTextureConfigurationScope.TextureConfigScope
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.render.client.model.objects.InflatedTexture

class InflatedTextureConfigurationScopeImpl(val ctx: SimpleModel.ModelContext) : InflatedTextureConfigurationScope {

  var tex: SimpleModel.PreparedTexture? = null

  override fun getQuads(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    return tex?.let { InflatedTexture(ctx.texture(it) as AtlasTexture).toList() }.orEmpty()
  }

  override fun texture(t: SimpleModel.PreparedTexture, op: TextureConfigScope.() -> Unit) {
    tex = t // TODO handle texture config
  }

}