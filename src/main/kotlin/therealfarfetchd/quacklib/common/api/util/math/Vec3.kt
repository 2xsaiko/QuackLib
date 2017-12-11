package therealfarfetchd.quacklib.common.api.util.math

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

data class Vec3(val x: Double, val y: Double, val z: Double) {
  constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
  constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())

  val xf = x.toFloat()
  val yf = y.toFloat()
  val zf = z.toFloat()

  operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
  operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
  operator fun times(f: Float) = Vec3(x * f, y * f, z * f)
  operator fun times(other: Vec3) = Vec3(x * other.x, y * other.y, z * other.z)
  operator fun div(f: Float) = Vec3(x / f, y / f, z / f)
  operator fun div(other: Vec3) = Vec3(x / other.x, y / other.y, z / other.z)

  operator fun unaryMinus() = Vec3(-x, -y, -z)

  val length by lazy { MathHelper.sqrt(x * x + y * y + z * z) }

  fun rotate(a: Double, axis: EnumFacing.Axis, center: Vec3): Vec3 {
    return if (a == 0.0 || center == this) this.copy()
    else when (axis) {
      EnumFacing.Axis.X -> {
        val r = Vec2(z, y).rotate(a, Vec2(center.z, center.y))
        Vec3(x, r.y, r.x)
      }
      EnumFacing.Axis.Y -> {
        val r = Vec2(x, z).rotate(a, Vec2(center.x, center.z))
        Vec3(r.x, y, r.y)
      }
      EnumFacing.Axis.Z -> {
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

  fun toVec3d() = Vec3d(x, y, z)
}

fun Vec3d.toVec3() = Vec3(x, y, z)