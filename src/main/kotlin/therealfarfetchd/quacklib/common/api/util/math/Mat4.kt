package therealfarfetchd.quacklib.common.api.util.math

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.BufferUtils
import java.nio.FloatBuffer
import javax.vecmath.Matrix4d

private val fb = BufferUtils.createFloatBuffer(16)

data class Mat4(
  val c00: Double, val c01: Double, val c02: Double, val c03: Double,
  val c10: Double, val c11: Double, val c12: Double, val c13: Double,
  val c20: Double, val c21: Double, val c22: Double, val c23: Double,
  val c30: Double, val c31: Double, val c32: Double, val c33: Double
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
    val jm = Matrix4d(c00, c01, c02, c03, c10, c11, c12, c13, c20, c21, c22, c23, c30, c31, c32, c33)
    jm.invert()
    Mat4(jm.m00, jm.m01, jm.m02, jm.m03, jm.m10, jm.m11, jm.m12, jm.m13, jm.m20, jm.m21, jm.m22, jm.m23, jm.m30, jm.m31, jm.m32, jm.m33)
  }

  fun translate(x: Double, y: Double, z: Double) = this * translateMat(x, y, z)

  fun translate(x: Float, y: Float, z: Float) = translate(x.toDouble(), y.toDouble(), z.toDouble())

  fun translate(xyz: Vec3) = this * translateMat(xyz)

  fun scale(x: Double, y: Double, z: Double) = this * scaleMat(x, y, z)

  fun scale(x: Float, y: Float, z: Float) = scale(x.toDouble(), y.toDouble(), z.toDouble())

  fun rotate(x: Double, y: Double, z: Double, angle: Double) = this * rotationMat(x, y, z, angle)

  fun rotate(x: Float, y: Float, z: Float, angle: Float) = rotate(x.toDouble(), y.toDouble(), z.toDouble(), angle.toDouble())

  companion object {
    @JvmStatic
    val Identity = Mat4(
      1.0, 0.0, 0.0, 0.0,
      0.0, 1.0, 0.0, 0.0,
      0.0, 0.0, 1.0, 0.0,
      0.0, 0.0, 0.0, 1.0
    )

    fun translateMat(x: Double, y: Double, z: Double) = Mat4(
      1.0, 0.0, 0.0, x,
      0.0, 1.0, 0.0, y,
      0.0, 0.0, 1.0, z,
      0.0, 0.0, 0.0, 1.0
    )

    fun translateMat(x: Float, y: Float, z: Float) = translateMat(x.toDouble(), y.toDouble(), z.toDouble())

    fun translateMat(xyz: Vec3) = translateMat(xyz.x, xyz.y, xyz.z)

    fun scaleMat(x: Double, y: Double, z: Double) = Mat4(
      x, 0.0, 0.0, 0.0,
      0.0, y, 0.0, 0.0,
      0.0, 0.0, z, 0.0,
      0.0, 0.0, 0.0, 1.0
    )

    fun scaleMat(x: Float, y: Float, z: Float) = scaleMat(x.toDouble(), y.toDouble(), z.toDouble())

    fun rotationMat(x: Double, y: Double, z: Double, angle: Double): Mat4 {
      val c = cos(-angle * toRadiansf)
      val s = sin(-angle * toRadiansf)
      val t = 1 - c

      return Mat4(
        t * x * x + c, t * x * y - s * z, t * x * z + s * y, 0.0,
        t * x * y + s * z, t * y * y + c, t * y * z - s * x, 0.0,
        t * x * z - s * y, t * y * z + s * x, t * z * z + c, 0.0,
        0.0, 0.0, 0.0, 1.0
      )
    }

    fun rotationMat(x: Float, y: Float, z: Float, angle: Float) =
      rotationMat(x.toDouble(), y.toDouble(), z.toDouble(), angle.toDouble())

    fun fromBuffer(fb: FloatBuffer) = Mat4(
      fb[0].toDouble(), fb[4].toDouble(), fb[8].toDouble(), fb[12].toDouble(),
      fb[1].toDouble(), fb[5].toDouble(), fb[9].toDouble(), fb[13].toDouble(),
      fb[2].toDouble(), fb[6].toDouble(), fb[10].toDouble(), fb[14].toDouble(),
      fb[3].toDouble(), fb[7].toDouble(), fb[11].toDouble(), fb[15].toDouble()
    )
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

operator fun Mat4.times(other: Vec3d) =
  (this * other.toVec3()).toVec3d()

@SideOnly(Side.CLIENT)
fun glMultMatrix(mat: Mat4) {
  fb.clear()
  listOf(mat.c00, mat.c10, mat.c20, mat.c30, mat.c01, mat.c11, mat.c21, mat.c31, mat.c02, mat.c12, mat.c22, mat.c32, mat.c03, mat.c13, mat.c23, mat.c33)
    .map(Double::toFloat)
    .forEach { fb.put(it) }
  fb.rewind()
  GlStateManager.multMatrix(fb)
}