package therealfarfetchd.quacklib.api.render.property

interface RenderPropertyConfigurationScope<C, T> {

  val name: String

  fun output(op: (C) -> T)

  fun constraints(op: (T) -> Boolean)

}