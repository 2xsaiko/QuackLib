package therealfarfetchd.quacklib.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.ObjConfigurationScope
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.render.model.objloader.OBJModelProvider
import therealfarfetchd.quacklib.tools.getResourceFromName

class ObjConfigurationScopeImpl(val ctx: SimpleModel.ModelContext) : ObjConfigurationScope {

  var rl: ResourceLocation? = null
  val textures = mutableMapOf<String, SimpleModel.PreparedTexture?>()

  override fun getQuads(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    val rl = rl ?: error("No OBJ model provided!")

    val data = OBJModelProvider.loadQuadsPrepared(rl, textures) ?: error("Failed loading OBJ")

    val output = mutableListOf<Quad>()

    output += data.quads
    for (o in data.objects) {
      output += o.value.quads
    }

    return output
  }


  override fun source(resource: ResourceLocation) {
    rl = fixRL(resource)
  }

  override fun source(resource: String) {
    source(getResourceFromName(resource))
  }

  fun fixRL(rl: ResourceLocation) = ResourceLocation(rl.namespace, "models/${rl.path}.obj")

  override fun texture(t: SimpleModel.PreparedTexture?, material0: String) {
    textures[material0] = t
  }

}