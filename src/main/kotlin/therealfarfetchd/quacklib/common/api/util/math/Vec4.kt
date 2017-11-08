package therealfarfetchd.quacklib.common.api.util.math

data class Vec4(val x: Float, val y: Float, val z: Float, val w: Float) {
  infix fun dotProduct(other: Vec4) = x * other.x + y * other.y + z * other.z + w * other.w
}

fun Vec3.toVec4() = Vec4(x, y, z, 1f)
fun Vec4.toVec3() = Vec3(x, y, z)