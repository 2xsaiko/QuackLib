package therealfarfetchd.quacklib.common.api.util.math

import net.minecraft.util.math.MathHelper
import therealfarfetchd.quacklib.common.api.extensions.pmod

const val toDegrees = 360.0 / (2.0 * Math.PI)
const val toRadians = (2.0 * Math.PI) / 360.0
const val toDegreesf = toDegrees.toFloat()
const val toRadiansf = toRadians.toFloat()

fun getDistance(x: Double, y: Double) =
  Math.sqrt(x * x + y * y)

/**
 * n-dimensional pythagorean theorem, just for the fun of it :P
 */
fun getDistance(vararg dimensions: Double) =
  Math.sqrt(dimensions.map { it * it }.sum())

fun getDistance(p1: Vec3, p2: Vec3) =
  getDistance(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z)

fun sin(f: Float): Float = MathHelper.sin(f)

fun cos(f: Float): Float = MathHelper.cos(f)

//fun tan(f: Float): Float = Math.tan(f.toDouble()).toFloat()
//
//fun asin(f: Float): Float = Math.asin(f.toDouble()).toFloat()
//
//fun acos(f: Float): Float = Math.acos(f.toDouble()).toFloat()
//
//fun atan2(y: Float, x: Float): Float = Math.atan2(y.toDouble(), x.toDouble()).toFloat()

fun sin(f: Double): Double = MathHelper.sin(f.toFloat()).toDouble()

fun cos(f: Double): Double = MathHelper.cos(f.toFloat()).toDouble()

//fun tan(f: Double): Double = Math.tan(f)
//
//fun asin(f: Double): Double = Math.asin(f)
//
//fun acos(f: Double): Double = Math.acos(f)
//
//fun atan2(y: Double, x: Double): Double = Math.atan2(y, x)
//
//fun sqrt(a: Double) = Math.sqrt(a)

/**
 * Something like modulo, but has a variable minimum instead of 0.
 * min: Minimum (inclusive)
 * max: Maximum (exclusive)
 */
fun wheel(min: Int, max: Int, v: Int) = ((v - min) pmod (max - min)) + min