package therealfarfetchd.quacklib.api.item.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <reified T> ItemComponentRenderProperties.renderProperty(name: String, noinline op: RenderPropertyConfigurationScope<Item, T>.() -> Unit): RenderProperty<*, Item, T> =
  QuackLibAPI.impl.addRenderPropertyItem(this, T::class, name, op)

// This is required because of a Kotlin issue: https://youtrack.jetbrains.com/issue/KT-17061
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
inline infix fun <reified C : ItemComponentRenderProperties, T> RenderProperty<*, Item, T>.fix(obj: C): RenderProperty<C, Item, T> {
  if (getComponentClass() != C::class) error("Invalid component class!")
  return this as RenderProperty<C, Item, T>
}