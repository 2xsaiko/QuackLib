package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.render.property.RenderPropertyBlock
import therealfarfetchd.quacklib.hax.Attachable
import therealfarfetchd.quacklib.hax.ExtraData

class ComponentRenderProps : Attachable<BlockComponentRenderProperties> {

  val props = mutableListOf<RenderPropertyBlock<*, *>>()

  companion object {
    val Key = ExtraData.createKey(::ComponentRenderProps)
  }

}