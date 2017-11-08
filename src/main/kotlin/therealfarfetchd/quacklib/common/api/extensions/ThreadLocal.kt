package therealfarfetchd.quacklib.common.api.extensions

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> threadLocal(init: () -> T): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
  val tl = object : ThreadLocal<T>() {
    override fun initialValue() = init()
  }

  override fun getValue(thisRef: Any, property: KProperty<*>) = tl.get()

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = tl.set(value)
}