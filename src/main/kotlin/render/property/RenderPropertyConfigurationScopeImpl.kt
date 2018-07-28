package therealfarfetchd.quacklib.render.property

import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

class RenderPropertyConfigurationScopeImpl<T>(override val name: String) : RenderPropertyConfigurationScope<T> {

  var outputOp: (Block) -> T = { TODO("not implemented") }

  val constraints = mutableListOf<(T) -> Boolean>()

  override fun output(op: (Block) -> T) {
    outputOp = op
  }

  override fun constraints(op: (T) -> Boolean) {
    constraints += op
  }

}