package therealfarfetchd.quacklib.render.model

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.Vec3i
import therealfarfetchd.quacklib.api.core.extensions.letIf
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.mkQuad
import therealfarfetchd.quacklib.api.render.model.BoxConfigurationScope
import therealfarfetchd.quacklib.api.render.model.BoxConfigurationScope.TextureConfigScope
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.tools.Facing
import kotlin.math.max
import kotlin.math.min

class BoxConfigurationScopeImpl(val ctx: SimpleModel.ModelContext) : BoxConfigurationScope {

  private var from = Vec3(0, 0, 0)
  private var to = Vec3(1, 1, 1)

  private var cull = true
  private var cullFace = BoxConfigurationScope.CullFace.Back

  private val textures = mutableMapOf<Facing, Pair<SimpleModel.PreparedTexture, TextureConfigScope.() -> Unit>>()

  @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
  override fun getQuads(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> {
    return textures.map { (facing, texture) ->
      val fv = facing.directionVec.toVec3i()
      val one = Vec3i(1, 1, 1)

      val v1 = when (facing.axisDirection) {
        EnumFacing.AxisDirection.POSITIVE -> to
        EnumFacing.AxisDirection.NEGATIVE -> from
      }

      val v2 = when (facing.axisDirection) {
        EnumFacing.AxisDirection.POSITIVE -> (one - fv) * from + fv * to
        EnumFacing.AxisDirection.NEGATIVE -> (one + fv) * to - fv * from
      }

      val (tex, mod) = texture

      val tc = TextureConfigScopeImpl().also(mod)

      tc.pipeQuad(mkQuad(getTexture(tex.resource), facing, v1, v2).letIf(facing.axisDirection == EnumFacing.AxisDirection.NEGATIVE) { it.flipTexturedSide })
    }
  }

  override fun from(x: Float, y: Float, z: Float) {
    from = Vec3(x / ctx.coordsScale, y / ctx.coordsScale, z / ctx.coordsScale)
    fixPoints()
  }

  override fun to(x: Float, y: Float, z: Float) {
    to = Vec3(x / ctx.coordsScale, y / ctx.coordsScale, z / ctx.coordsScale)
    fixPoints()
  }

  private fun fixPoints() {
    val minX = min(from.x, to.x)
    val minY = min(from.y, to.y)
    val minZ = min(from.z, to.z)
    val maxX = max(from.x, to.x)
    val maxY = max(from.y, to.y)
    val maxZ = max(from.z, to.z)

    from = Vec3(minX, minY, minZ)
    to = Vec3(maxX, maxY, maxZ)
  }

  override fun cull(b: Boolean) {
    cull = b
  }

  override fun cullFace(s: BoxConfigurationScope.CullFace) {
    cullFace = s
  }

  override fun texture(t: SimpleModel.PreparedTexture?, side0: Facing, op: TextureConfigScope.() -> Unit) {
    if (t == null) textures.remove(side0)
    else textures[side0] = Pair(t, op)
  }

}