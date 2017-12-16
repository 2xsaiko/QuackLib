package therealfarfetchd.quacklib.common.api.util.math

import kotlin.math.atan2

data class Vec2(val x: Double, val y: Double) {
  constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
  constructor(x: Float, y: Float) : this(x.toDouble(), y.toDouble())

  val xf = x.toFloat()
  val yf = y.toFloat()

  operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
  operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
  operator fun div(other: Vec2) = Vec2(x / other.x, y / other.y)
  operator fun times(other: Vec2) = Vec2(x * other.x, y * other.y)

  operator fun plus(other: Double) = Vec2(x + other, y + other)
  operator fun minus(other: Double) = Vec2(x + other, y + other)
  operator fun div(other: Double) = Vec2(x / other, y / other)
  operator fun times(other: Double) = Vec2(x * other, y * other)

  operator fun plus(other: Float) = Vec2(x + other, y + other)
  operator fun minus(other: Float) = Vec2(x + other, y + other)
  operator fun div(other: Float) = Vec2(x / other, y / other)
  operator fun times(other: Float) = Vec2(x * other, y * other)

  operator fun plus(other: Int) = Vec2(x + other, y + other)
  operator fun minus(other: Int) = Vec2(x + other, y + other)
  operator fun div(other: Int) = Vec2(x / other, y / other)
  operator fun times(other: Int) = Vec2(x * other, y * other)

  fun rotate(a: Double, center: Vec2): Vec2 {
    return if (a == 0.0 || center == this) this.copy()
    else {
      val x1 = x - center.x
      val y1 = y - center.y

      val h = getDistance(x1, y1)
      val angle = toRadians * (toDegrees * (atan2(y1, x1)) + a)
      val nx = cos(angle) * h // can't use MathHelper.cos/sin here, it returns 1-[expected value] for some cases >.<
      val ny = sin(angle) * h
      Vec2(nx + center.x, ny + center.y)
    }
  }
}