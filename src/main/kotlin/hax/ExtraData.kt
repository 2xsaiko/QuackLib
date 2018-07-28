package therealfarfetchd.quacklib.hax

import java.util.*

/**
 * Use with caution; memory leaks possible! Don't store a reference to the main object in the attached data!
 */
object ExtraData {

  private val map = WeakHashMap<Any, ObjectAttachedData<*>>()

  @Suppress("UNCHECKED_CAST")
  private fun <R> getData(obj: R): ObjectAttachedData<R> {
    return map.computeIfAbsent(obj) { ObjectAttachedData<R>() } as ObjectAttachedData<R>
  }

  fun <R, T : Attachable<R>, K : DataKey<R, T>> attach(obj: R, key: K) {
    val data = getData(obj)
    val newinst = key.create()
    data.data[key] = newinst
  }

  fun <R, T : Attachable<R>, K : DataKey<R, T>> exists(obj: R, key: K): Boolean {
    val data = getData(obj)
    return key in data.data
  }

  @Suppress("UNCHECKED_CAST")
  fun <R, T : Attachable<R>, K : DataKey<R, T>> get(obj: R, key: K, createIfNecessary: Boolean = true): T {
    if (!exists(obj, key) && !createIfNecessary) error("Attachable not present!")
    val data = getData(obj)
    return data.data.computeIfAbsent(key) { key.create() } as T
  }

  @Suppress("UNCHECKED_CAST")
  fun <R, T : Attachable<R>, K : DataKey<R, T>> getOrNull(obj: R, key: K): T? {
    if (!exists(obj, key)) return null
    val data = getData(obj)
    return data.data[key] as T
  }

  fun <R, T : Attachable<R>, K : DataKey<R, T>> delete(obj: R, key: K) {
    if (!exists(obj, key)) return
    val data = getData(obj)
    data.data.remove(key)
  }

  fun <R, T : Attachable<R>> createKey(a: () -> T): DataKey<R, T> {
    return DataKeyImpl(a)
  }

  private class ObjectAttachedData<R> {

    val data = mutableMapOf<DataKey<R, Attachable<R>>, Attachable<R>>()

  }

  private class DataKeyImpl<in R, out T : Attachable<R>>(val c: () -> T) : DataKey<R, T> {

    override fun create(): T = c()

  }

}

interface DataKey<in R, out T : Attachable<R>> {

  fun create(): T

}

@Suppress("unused")
interface Attachable<in R>