package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <reified T> BlockComponentRenderProperties.renderProperty(name: String, noinline op: RenderPropertyConfigurationScope<T>.() -> Unit): RenderProperty<*, T> =
  QuackLibAPI.impl.addRenderProperty(this, T::class, name, op)

// This is required because of a Kotlin issue: https://youtrack.jetbrains.com/issue/KT-17061
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
inline infix fun <reified C : BlockComponentRenderProperties, T> RenderProperty<*, T>.fix(obj: C): RenderProperty<C, T> {
  if (getComponentClass() != C::class) error("Invalid component class!")
  return this as RenderProperty<C, T>
}