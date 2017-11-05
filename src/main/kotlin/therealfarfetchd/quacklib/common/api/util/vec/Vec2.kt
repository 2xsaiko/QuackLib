package therealfarfetchd.quacklib.common.api.util.vec

import net.minecraft.util.math.MathHelper

data class Vec2(val x: Float, val y: Float) {
  operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
  operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
  operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)
  operator fun times(other: Vec2) = Vec2(x * other.x, y * other.y)

  operator fun plus(other: Float) = Vec2(x + other, y + other)
  operator fun minus(other: Float) = Vec2(x + other, y + other)
  operator fun div(other: Float) = Vec2(x / other, y / other)
  operator fun times(other: Float) = Vec2(x * other, y * other)

  operator fun plus(other: Int) = Vec2(x + other, y + other)
  operator fun minus(other: Int) = Vec2(x + other, y + other)
  operator fun div(other: Int) = Vec2(x / other, y / other)
  operator fun times(other: Int) = Vec2(x * other, y * other)

  fun rotate(a: Float, center: Vec2): Vec2 {
    return if (a == 0.0F || center == this) this.copy()
    else {
      val x1 = x - center.x
      val y1 = y - center.y
      val h = MathHelper.sqrt(x1 * x1 + y1 * y1)
      val angle = Math.toRadians(Math.toDegrees(Math.atan2(y1.toDouble(), x1.toDouble())) + a)
      val nx = Math.cos(angle) * h // can't use MathHelper.cos/sin here, it returns 1-[expected value] for some cases >.<
      val ny = Math.sin(angle) * h
      Vec2((nx + center.x).toFloat(), (ny + center.y).toFloat())
    }
  }
}