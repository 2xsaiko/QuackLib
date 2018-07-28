package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <reified T> BlockComponentRenderProperties.renderProperty(name: String, noinline op: RenderPropertyConfigurationScope<T>.() -> Unit): RenderProperty<T> =
  QuackLibAPI.impl.addRenderProperty(this, T::class, name, op)
