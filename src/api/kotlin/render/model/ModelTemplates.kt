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

  val InflatedTexture: ObjectBuilderProvider<InflatedTextureConfigurationScope>

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

  fun texture(t: SimpleModel.PreparedTexture?, side0: Facing, op: TextureConfigScope.() -> Unit = {})

  fun texture(t: SimpleModel.PreparedTexture?, side0: Facing, vararg side: Facing, op: TextureConfigScope.() -> Unit = {}) {
    texture(t, side0)
    for (s in side) texture(t, s, op)
  }

  fun textureAll(t: SimpleModel.PreparedTexture?, op: TextureConfigScope.() -> Unit = {}) {
    for (s in Facing.VALUES) texture(t, s, op)
  }

  interface TextureConfigScope {

    fun uv(x1: Float, y1: Float, x2: Float, y2: Float)

    /**
     * Rotates the texture by the specified angle. 90° increments only.
     */
    fun rotate(angle: Int)

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

interface InflatedTextureConfigurationScope : ModelConfigurationScope {

  fun texture(t: SimpleModel.PreparedTexture, op: TextureConfigScope.() -> Unit = {})

  interface TextureConfigScope {

    fun uv(x1: Float, y1: Float, x2: Float, y2: Float)

    /**
     * Rotates the texture by the specified angle. 90° increments only.
     */
    fun rotate(angle: Int)

  }

}