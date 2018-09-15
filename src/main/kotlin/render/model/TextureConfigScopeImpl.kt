package therealfarfetchd.quacklib.render.model

import therealfarfetchd.math.Vec2
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.BoxConfigurationScope
import therealfarfetchd.quacklib.api.render.model.InflatedTextureConfigurationScope

class TextureConfigScopeImpl : InflatedTextureConfigurationScope.TextureConfigScope, BoxConfigurationScope.TextureConfigScope {

  var uv: Pair<Vec2, Vec2>? = null
  var rotation: Int = 0

  override fun uv(x1: Float, y1: Float, x2: Float, y2: Float) {
    uv = Pair(Vec2(x1, y1), Vec2(x2, y2))
  }

  override fun rotate(angle: Int) {
    require(angle % 90 == 0) { "Angle must be a multiple of 90°!" }
    rotation += angle
  }

  fun pipeQuad(q: Quad): Quad {
    // grrr that shouldn't be a warning, just let me make parameters mutable
    @Suppress("NAME_SHADOWING")
    var q = q

    uv?.also { uv ->
      val (v1, v3) = uv
      val v2 = Vec2(v3.x, v1.y)
      val v4 = Vec2(v1.x, v3.y)

      q = q.copy(tex1 = v1, tex2 = v2, tex3 = v3, tex4 = v4)
    }

    return q.rotateTexture(rotation)
  }

}