package therealfarfetchd.quacklib.common.api.util

import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis.*
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

data class Vec3(val x: Float, val y: Float, val z: Float) {
  constructor(x: Double, y: Double, z: Double) : this(x.toFloat(), y.toFloat(), z.toFloat())

  operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
  operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
  operator fun times(f: Float) = Vec3(x * f, y * f, z * f)
  operator fun times(other: Vec3) = Vec3(x * other.x, y * other.y, z * other.z)
  operator fun div(f: Float) = Vec3(x / f, y / f, z / f)
  operator fun div(other: Vec3) = Vec3(x / other.x, y / other.y, z / other.z)

  operator fun unaryMinus() = Vec3(-x, -y, -z)

  val length by lazy { MathHelper.sqrt(x * x + y * y + z * z) }

  fun rotate(a: Float, axis: EnumFacing.Axis, center: Vec3): Vec3 {
    return if (a == 0.0F || center == this) this.copy()
    else when (axis) {
      X -> {
        val r = Vec2(z, y).rotate(a, Vec2(center.z, center.y))
        Vec3(x, r.y, r.x)
      }
      Y -> {
        val r = Vec2(x, z).rotate(a, Vec2(center.x, center.z))
        Vec3(r.x, y, r.y)
      }
      Z -> {
        val r = Vec2(x, y).rotate(a, Vec2(center.x, center.y))
        Vec3(r.x, r.y, z)
      }
    }
  }

  infix fun crossProduct(other: Vec3): Vec3 {
    val cx = y * other.z - z * other.y
    val cy = z * other.x - x * other.z
    val cz = x * other.y - y * other.x
    return Vec3(cx, cy, cz)
  }

  fun normalize() = this / length

  fun toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
}

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