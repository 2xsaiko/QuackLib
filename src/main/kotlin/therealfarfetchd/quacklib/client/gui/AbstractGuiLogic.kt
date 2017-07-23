package therealfarfetchd.quacklib.client.gui

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class AbstractGuiLogic {

  lateinit var root: QGuiScreen.ScreenRoot

  lateinit var params: Map<String, Any?>

  abstract fun init()

  fun update() {}

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

  @Suppress("UNCHECKED_CAST")
  protected fun <T> params(): ReadOnlyProperty<Any?, T> = object : ReadOnlyProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = params[property.name] as T
  }

  protected inline fun <reified T : Any> component(): ReadOnlyProperty<Any?, T> = object : ReadOnlyProperty<Any?, T> {
    var el: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
      if (el == null) el = component(property.name)
      return el!!
    }
  }

}

class NullGuiLogic : AbstractGuiLogic() {
  override fun init() {}
}