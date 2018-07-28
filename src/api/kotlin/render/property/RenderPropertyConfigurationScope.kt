package therealfarfetchd.quacklib.api.render.property

import therealfarfetchd.quacklib.api.objects.block.Block

interface RenderPropertyConfigurationScope<T> {

  val name: String

  fun output(op: (Block) -> T)

  fun constraints(op: (T) -> Boolean)

}