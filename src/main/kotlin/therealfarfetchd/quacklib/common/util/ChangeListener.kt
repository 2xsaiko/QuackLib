package therealfarfetchd.quacklib.common.util

import kotlin.reflect.KProperty0

/**
 * Created by marco on 13.07.17.
 */
class ChangeListener(vararg val values: KProperty0<*>) {

  private var map: Map<KProperty0<*>, Any?> = mkMap()

  private fun mkMap(): Map<KProperty0<*>, Any?> = values.associate { it to tryCopy(it.get()) }

  fun valuesChanged(update: Boolean = true): Boolean {
    val new = mkMap()
    val result = map contentDeepEquals new
    if (update) map = new
    return !result
  }

  private infix fun <K, V> Map<K, V>.contentDeepEquals(other: Map<K, V>): Boolean {
    return (this.keys + other.keys).all { key ->
      val tv = this[key]
      val ov = other[key]
      if (tv is Array<*> && ov is Array<*>) tv contentDeepEquals ov
      else if (tv is BooleanArray && ov is BooleanArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is ByteArray && ov is ByteArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is ShortArray && ov is ShortArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is IntArray && ov is IntArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is LongArray && ov is LongArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is FloatArray && ov is FloatArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else if (tv is DoubleArray && ov is DoubleArray) tv.toTypedArray() contentDeepEquals ov.toTypedArray()
      else tv == ov
    }
  }

  private fun tryCopy(a: Any?): Any? {
    return when (a) {
      is Array<*> -> a.copyOf()
      is BooleanArray -> a.copyOf()
      is ByteArray -> a.copyOf()
      is ShortArray -> a.copyOf()
      is IntArray -> a.copyOf()
      is LongArray -> a.copyOf()
      is FloatArray -> a.copyOf()
      is DoubleArray -> a.copyOf()
      else -> a
    }
  }

}