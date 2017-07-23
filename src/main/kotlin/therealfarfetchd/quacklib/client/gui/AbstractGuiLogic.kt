package therealfarfetchd.quacklib.client.gui

abstract class AbstractGuiLogic {

  lateinit var root: QGuiScreen.ScreenRoot

  lateinit var params: Map<String, Any?>

  protected fun flatElements(ge: IGuiElement = root): Set<GuiElement> {
    return ge.elements + ge.elements.flatMap(this::flatElements)
  }

  protected inline fun <reified T> component(name: String): T {
    return flatElements().find { name == it.name } as? T ?: throw IllegalStateException("Couldn't find object $name (${T::class}")
  }

  @Suppress("UNCHECKED_CAST")
  protected inline fun <reified T : GuiElement> T.action(noinline op: T.() -> Any?) {
    this.action = op as GuiElement.() -> Any?
  }

  abstract fun init()

  fun update() {}

}

class NullGuiLogic : AbstractGuiLogic() {
  override fun init() {}
}