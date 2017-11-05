package therealfarfetchd.quacklib.common.api.util

import net.minecraft.util.math.MathHelper

object MathUtils {
  const val toDegrees = 360.0 / (2.0 * Math.PI)
  const val toRadians = (2.0 * Math.PI) / 360.0

  fun getDistance(x: Float, y: Float): Float =
    MathHelper.sqrt(x * x + y * y)

  /**
   * n-dimensional pythagorean theorem, just for the fun of it :P
   */
  fun getDistance(vararg dimensions: Float) =
    MathHelper.sqrt(dimensions.map { it * it }.sum())
}