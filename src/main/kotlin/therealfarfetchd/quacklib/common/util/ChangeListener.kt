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
    if (a is Array<*>) return a.copyOf()
    else if (a is BooleanArray) return a.copyOf()
    else if (a is ByteArray) return a.copyOf()
    else if (a is ShortArray) return a.copyOf()
    else if (a is IntArray) return a.copyOf()
    else if (a is LongArray) return a.copyOf()
    else if (a is FloatArray) return a.copyOf()
    else if (a is DoubleArray) return a.copyOf()
    else return a
  }

}