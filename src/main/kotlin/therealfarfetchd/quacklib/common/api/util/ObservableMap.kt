package therealfarfetchd.quacklib.common.api.util

import therealfarfetchd.quacklib.common.api.extensions.filterNotNull
import therealfarfetchd.quacklib.common.api.extensions.filterValuesNotNull

class ObservableMap<K, V>(val wrap: MutableMap<K, V> = HashMap()) : MutableMap<K, V> {
  private var read: Map<K, () -> V> = emptyMap()

  private var write: Map<K, (V) -> Any?> = emptyMap()

  override val size: Int
    get() = (wrap.keys + read.keys).toSet().size

  fun overrideRead(key: K, op: () -> V) {
    read += key to op
  }

  fun overrideWrite(key: K, op: (V) -> Any?) {
    write += key to op
  }

  override fun containsKey(key: K): Boolean {
    return wrap.containsKey(key) || read.containsKey(key)
  }

  override fun containsValue(value: V): Boolean {
    return wrap.containsValue(value) || read.any { it.value() == value }
  }

  override fun get(key: K): V? {
    return read[key]?.invoke() ?: wrap[key]
  }

  override fun isEmpty(): Boolean {
    return wrap.isEmpty() && read.all { it.value() == null }
  }

  override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
    get() = keys.map { it to get(it) }.toMap().filterValuesNotNull().toMutableMap().entries

  override val keys: MutableSet<K>
    get() = (wrap.keys + read.keys).toMutableSet()

  override val values: MutableCollection<V>
    get() = keys.map { get(it) }.filterNotNull().toMutableList()

  override fun clear() {
    wrap.clear()
  }

  override fun put(key: K, value: V): V? {
    val old = get(key)
    if (key in write) write[key]!!.invoke(value)
    else wrap[key] = value
    return old
  }

  override fun putAll(from: Map<out K, V>) {
    from.forEach { k, v -> put(k, v) }
  }

  override fun remove(key: K): V? {
    val v = get(key)
    wrap.remove(key)
    return v
  }

}