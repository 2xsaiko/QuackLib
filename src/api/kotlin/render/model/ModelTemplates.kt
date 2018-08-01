package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.tools.Facing

@Suppress("PropertyName")
interface ModelTemplates {

  val Box: ObjectBuilderProvider<BoxConfigurationScope>

  val OBJ: ObjectBuilderProvider<ObjConfigurationScope>

}

@SimpleModelDSL
interface ModelConfigurationScope {
  fun getQuads(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>
}

interface BoxConfigurationScope : ModelConfigurationScope {

  fun from(v: Vec3) = from(v.x, v.y, v.z)
  fun to(v: Vec3) = to(v.x, v.y, v.z)
  fun from(x: Float, y: Float, z: Float)
  fun to(x: Float, y: Float, z: Float)

  fun cull(b: Boolean)
  fun cullFace(s: CullFace)

  fun texture(t: SimpleModel.PreparedTexture?, side0: Facing)

  fun texture(t: SimpleModel.PreparedTexture?, side0: Facing, vararg side: Facing) {
    texture(t, side0)
    for (s in side) texture(t, s)
  }

  fun textureAll(t: SimpleModel.PreparedTexture?) {
    for (s in Facing.VALUES) texture(t, s)
  }

  enum class CullFace {
    Front,
    Back
  }

}

interface ObjConfigurationScope : ModelConfigurationScope {

  fun source(resource: ResourceLocation)
  fun source(resource: String)

  fun texture(t: SimpleModel.PreparedTexture?, material0: String)
  fun texture(t: SimpleModel.PreparedTexture?, material0: String, vararg material: String) {
    texture(t, material0)
    for (m in material) texture(t, m)
  }

}