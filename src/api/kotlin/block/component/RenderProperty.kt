package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.render.property.RenderPropertyBlock
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

@Suppress("unused", "NOTHING_TO_INLINE")
inline fun <reified T> BlockComponentRenderProperties.renderProperty(name: String, noinline op: RenderPropertyConfigurationScope<Block, T>.() -> Unit): RenderPropertyBlock<*, T> =
  QuackLibAPI.impl.addRenderPropertyBlock(this, T::class, name, op)

// This is required because of a Kotlin issue: https://youtrack.jetbrains.com/issue/KT-17061
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
inline infix fun <reified C : BlockComponentRenderProperties, T> RenderPropertyBlock<*, T>.fix(obj: C): RenderPropertyBlock<C, T> {
  if (getComponentClass() != C::class) error("Invalid component class!")
  return this as RenderPropertyBlock<C, T>
}