package therealfarfetchd.quacklib.api.tools

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

inline fun <reified T : Any, reified R> T.access(name: String, type: KClass<T> = T::class): R {
  val field = type.java.getDeclaredField(name)
  field.isAccessible = true
  require(R::class.java.isAssignableFrom(field.type), { "Field $name is not of type ${R::class}" })
  return field[this] as R
}

inline fun <reified T : Any, reified R> T.accessDelegate(name: String, type: KClass<T> = T::class) = object : ReadWriteProperty<Any, R> {

  val getter: (T) -> R
  val setter: (T, R) -> Unit

  init {
    val field = type.java.getDeclaredField(name)
    field.isAccessible = true
    require(R::class.java.isAssignableFrom(field.type), { "Field $name is not of type ${R::class}" })

    // Kotlin doesn't like these (╯°□°）╯︵ ┻━┻

    // val mhg = MethodHandles.lookup().unreflectGetter(field)
    // val mhs = MethodHandles.lookup().unreflectSetter(field)

    getter = { t -> field[t] as R }
    setter = { t, r -> field[t] = r }
  }

  override fun getValue(thisRef: Any, property: KProperty<*>): R {
    return getter(this@accessDelegate)
  }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: R) {
    setter(this@accessDelegate, value)
  }

}

inline fun <T : Any, reified R> KClass<T>.access(name: String): R {
  val field = java.getDeclaredField(name)
  field.isAccessible = true
  require(R::class.java.isAssignableFrom(field.type), { "Field $name is not of type ${R::class}" })
  return field[null] as R
}