package therealfarfetchd.quacklib.render.property

import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope

class RenderPropertyConfigurationScopeImpl<C, T>(override val name: String) : RenderPropertyConfigurationScope<C, T> {

  var outputOp: (C) -> T = { TODO("not implemented") }

  val constraints = mutableListOf<(T) -> Boolean>()

  override fun output(op: (C) -> T) {
    outputOp = op
  }

  override fun constraints(op: (T) -> Boolean) {
    constraints += op
  }

}