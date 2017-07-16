package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.gui.GuiScreen

/**
 * Created by marco on 16.07.17.
 */
class QGuiScreen : GuiScreen() {

  val root = object : IGuiElement {
    override val width: Int
      get() = this@QGuiScreen.width

    override val height: Int
      get() = this@QGuiScreen.height

    override var elements: Set<GuiElement> = emptySet()

    override fun render(mouseX: Int, mouseY: Int) {
      drawDefaultBackground()
      elements.forEach { it.transformAndRender(mouseX, mouseY) }
    }
  }

  override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    root.render(mouseX, mouseY)
  }

}