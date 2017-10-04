package therealfarfetchd.quacklib.common.api.extensions

/**
 * Created by marco on 13.07.17.
 */

inline fun <reified T> Collection<T>.copyTo(array: Array<T>, arrStart: Int = 0) = this.toTypedArray().copyTo(array, arrStart)

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

@Suppress("UNCHECKED_CAST")
fun <T> Collection<T?>.filterNotNull(): List<T> {
  return filter { it != null } as List<T>
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> {
  return filterKeys { it != null } as Map<K, V>
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> {
  return filterValues { it != null } as Map<K, V>
}

inline fun <T, R> Array<T>.mapWithCopy(op: (T) -> R): List<Pair<T, R>> = map { it to op(it) }

inline fun <T, R> Collection<T>.mapWithCopy(op: (T) -> R): List<Pair<T, R>> = map { it to op(it) }

inline fun <A, B, R> Array<Pair<A, B>>.mapFirst(op: (A) -> R): List<Pair<R, B>> = map { op(it.first) to it.second }

inline fun <A, B, R> Collection<Pair<A, B>>.mapFirst(op: (A) -> R): List<Pair<R, B>> = map { op(it.first) to it.second }

inline fun <A, B, R> Array<Pair<A, B>>.mapSecond(op: (B) -> R): List<Pair<A, R>> = map { it.first to op(it.second) }

inline fun <A, B, R> Collection<Pair<A, B>>.mapSecond(op: (B) -> R): List<Pair<A, R>> = map { it.first to op(it.second) }

@Suppress("UNCHECKED_CAST")
fun <A, B, R> Collection<Pair<A, B>>.mapFirstNotNull(op: (A) -> R?): List<Pair<R, B>> {
  return mapFirst(op).filterNot { it.first == null } as List<Pair<R, B>>
}

@Suppress("UNCHECKED_CAST")
fun <A, B, R> Array<Pair<A, B>>.mapFirstNotNull(op: (A) -> R?): List<Pair<R, B>> {
  return mapFirst(op).filterNot { it.first == null } as List<Pair<R, B>>
}

fun <A, B> Collection<Pair<A?, B>>.mapFirstNotNull(): List<Pair<A, B>> = mapFirstNotNull { it }

fun <A, B> Array<Pair<A?, B>>.mapFirstNotNull(): List<Pair<A, B>> = mapFirstNotNull { it }

@Suppress("UNCHECKED_CAST")
inline fun <A, B, R> Collection<Pair<A, B>>.mapSecondNotNull(op: (B) -> R?): List<Pair<A, R>> {
  return mapSecond(op).filterNot { it.second == null } as List<Pair<A, R>>
}

@Suppress("UNCHECKED_CAST")
inline fun <A, B, R> Array<Pair<A, B>>.mapSecondNotNull(op: (B) -> R?): List<Pair<A, R>> {
  return mapSecond(op).filterNot { it.second == null } as List<Pair<A, R>>
}

fun <A, B> Collection<Pair<A, B?>>.mapSecondNotNull(): List<Pair<A, B>> = mapSecondNotNull { it }

fun <A, B> Array<Pair<A, B?>>.mapSecondNotNull(): List<Pair<A, B>> = mapSecondNotNull { it }

fun <A, B> Pair<A, B>.swap(): Pair<B, A> = second to first