package therealfarfetchd.quacklib.api.objects

import kotlin.reflect.KClass

interface ComponentHost<C : Any> {

  val components: List<C>

  fun <T : C> getComponentsOfType(type: KClass<T>): List<T> =
    components.filterIsInstance(type.java)

  fun hasComponent(type: KClass<out C>): Boolean =
    components.any(type::isInstance)

}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> ComponentHost<*>.getComponentsOfType(): List<T> =
  (this as ComponentHost<Any>).getComponentsOfType(T::class)

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Any> ComponentHost<*>.hasComponent(): Boolean =
  (this as ComponentHost<Any>).hasComponent(T::class)