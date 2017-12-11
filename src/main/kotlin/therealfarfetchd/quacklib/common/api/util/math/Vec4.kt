package therealfarfetchd.quacklib.common.api.util.math

data class Vec4(val x: Double, val y: Double, val z: Double, val w: Double) {
  infix fun dotProduct(other: Vec4) = x * other.x + y * other.y + z * other.z + w * other.w
}

fun Vec3.toVec4() = Vec4(x, y, z, 1.0)
fun Vec4.toVec3() = Vec3(x / w, y / w, z / w)