package therealfarfetchd.quacklib.common.api.util.math

import net.minecraft.util.math.MathHelper
import javax.vecmath.Matrix4f

data class Mat4(
  val c00: Float, val c01: Float, val c02: Float, val c03: Float,
  val c10: Float, val c11: Float, val c12: Float, val c13: Float,
  val c20: Float, val c21: Float, val c22: Float, val c23: Float,
  val c30: Float, val c31: Float, val c32: Float, val c33: Float
) {
  val r0 by lazy { Vec4(c00, c01, c02, c03) }
  val r1 by lazy { Vec4(c10, c11, c12, c13) }
  val r2 by lazy { Vec4(c20, c21, c22, c23) }
  val r3 by lazy { Vec4(c30, c31, c32, c33) }

  val c0 by lazy { Vec4(c00, c10, c20, c30) }
  val c1 by lazy { Vec4(c01, c11, c21, c31) }
  val c2 by lazy { Vec4(c02, c12, c22, c32) }
  val c3 by lazy { Vec4(c03, c13, c23, c33) }

  // I'm not even going to bother
  val inverse by lazy {
    val jm = Matrix4f(c00, c01, c02, c03, c10, c11, c12, c13, c20, c21, c22, c23, c30, c31, c32, c33)
    jm.invert()
    Mat4(jm.m00, jm.m01, jm.m02, jm.m03, jm.m10, jm.m11, jm.m12, jm.m13, jm.m20, jm.m21, jm.m22, jm.m23, jm.m30, jm.m31, jm.m32, jm.m33)
  }

  fun translate(x: Float, y: Float, z: Float) = this * translateMat(x, y, z)

  fun translate(xyz: Vec3) = this * translateMat(xyz)

  fun scale(x: Float, y: Float, z: Float) = this * scaleMat(x, y, z)

  fun rotate(x: Float, y: Float, z: Float, angle: Float) = this * rotationMat(x, y, z, angle)

  companion object {
    @JvmStatic
    val Identity = Mat4(
      1f, 0f, 0f, 0f,
      0f, 1f, 0f, 0f,
      0f, 0f, 1f, 0f,
      0f, 0f, 0f, 1f
    )

    fun translateMat(x: Float, y: Float, z: Float) = Mat4(
      1f, 0f, 0f, x,
      0f, 1f, 0f, y,
      0f, 0f, 1f, z,
      0f, 0f, 0f, 1f
    )

    fun translateMat(xyz: Vec3) = translateMat(xyz.x, xyz.y, xyz.z)

    fun scaleMat(x: Float, y: Float, z: Float) = Mat4(
      x, 0f, 0f, 0f,
      0f, y, 0f, 0f,
      0f, 0f, z, 0f,
      0f, 0f, 0f, 1f
    )

    fun rotationMat(x: Float, y: Float, z: Float, angle: Float): Mat4 {
      val c = MathHelper.cos(-angle * MathUtils.toRadians.toFloat())
      val s = MathHelper.sin(-angle * MathUtils.toRadians.toFloat())
      val t = 1 - c

      return Mat4(
        t * x * x + c, t * x * y - s * z, t * x * z + s * y, 0f,
        t * x * y + s * z, t * y * y + c, t * y * z - s * x, 0f,
        t * x * z - s * y, t * y * z + s * x, t * z * z + c, 0f,
        0f, 0f, 0f, 1f
      )
    }
  }
}

operator fun Mat4.plus(other: Mat4) = Mat4(
  c00 + other.c00, c01 + other.c01, c02 + other.c02, c03 + other.c03,
  c10 + other.c10, c11 + other.c11, c12 + other.c12, c13 + other.c13,
  c20 + other.c20, c21 + other.c21, c22 + other.c22, c23 + other.c23,
  c30 + other.c30, c31 + other.c31, c32 + other.c32, c33 + other.c33
)

operator fun Float.times(other: Mat4) = Mat4(
  this * other.c00, this * other.c01, this * other.c02, this * other.c03,
  this * other.c10, this * other.c11, this * other.c12, this * other.c13,
  this * other.c20, this * other.c21, this * other.c22, this * other.c23,
  this * other.c30, this * other.c31, this * other.c32, this * other.c33
)

operator fun Mat4.times(other: Mat4) = Mat4(
  r0 dotProduct other.c0, r0 dotProduct other.c1, r0 dotProduct other.c2, r0 dotProduct other.c3,
  r1 dotProduct other.c0, r1 dotProduct other.c1, r1 dotProduct other.c2, r1 dotProduct other.c3,
  r2 dotProduct other.c0, r2 dotProduct other.c1, r2 dotProduct other.c2, r2 dotProduct other.c3,
  r3 dotProduct other.c0, r3 dotProduct other.c1, r3 dotProduct other.c2, r3 dotProduct other.c3
)

operator fun Mat4.times(other: Vec4) =
  Vec4(r0 dotProduct other, r1 dotProduct other, r2 dotProduct other, r3 dotProduct other)

operator fun Mat4.times(other: Vec3) =
  (this * other.toVec4()).toVec3()