package therealfarfetchd.quacklib.api.render.texture

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec2i

interface Texture {

  /**
   * The top left coordinates of this texture. For normal textures, this is usually (0, 0).
   */
  val min: Vec2

  /**
   * The bottom right coordinates of this texture. For normal textures, this is usually (1, 1).
   */
  val max: Vec2

  /**
   * The texture location of this texture to use when binding.
   */
  val texture: ResourceLocation

  /**
   * The size of the texture, in pixels
   */
  val size: Vec2i

  /**
   * Maps the passed coordinates from (0,0) → (1,1) to min → max.
   */
  fun interpolate(uv: Vec2): Vec2 {
    val f = max - min
    return min + f * uv
  }

}