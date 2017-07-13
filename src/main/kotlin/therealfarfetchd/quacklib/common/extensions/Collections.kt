package therealfarfetchd.quacklib.common.extensions

/**
 * Created by marco on 13.07.17.
 */

inline fun <reified T> Collection<T>.copyTo(array: Array<T>, arrStart: Int = 0) = this.toTypedArray().copyTo(array, 0)

inline fun <reified T> Array<T>.copyTo(array: Array<T>, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun BooleanArray.copyTo(array: BooleanArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Boolean>.copyTo(array: BooleanArray, arrStart: Int = 0) = this.toBooleanArray().copyTo(array, arrStart)

fun ByteArray.copyTo(array: ByteArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Byte>.copyTo(array: ByteArray, arrStart: Int = 0) = this.toByteArray().copyTo(array, arrStart)

fun ShortArray.copyTo(array: ShortArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Short>.copyTo(array: ShortArray, arrStart: Int = 0) = this.toShortArray().copyTo(array, arrStart)

fun CharArray.copyTo(array: CharArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Char>.copyTo(array: CharArray, arrStart: Int = 0) = this.toCharArray().copyTo(array, arrStart)

fun IntArray.copyTo(array: IntArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Int>.copyTo(array: IntArray, arrStart: Int = 0) = this.toIntArray().copyTo(array, arrStart)

fun LongArray.copyTo(array: LongArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Long>.copyTo(array: LongArray, arrStart: Int = 0) = this.toLongArray().copyTo(array, arrStart)

fun FloatArray.copyTo(array: FloatArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Float>.copyTo(array: FloatArray, arrStart: Int = 0) = this.toFloatArray().copyTo(array, arrStart)

fun DoubleArray.copyTo(array: DoubleArray, arrStart: Int = 0) = System.arraycopy(this, 0, array, arrStart, minOf(this.size, array.size - arrStart))

fun Collection<Double>.copyTo(array: DoubleArray, arrStart: Int = 0) = this.toDoubleArray().copyTo(array, arrStart)
