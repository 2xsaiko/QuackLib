package therealfarfetchd.quacklib.client.gui.elements

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.client.gui.GuiElement
import therealfarfetchd.quacklib.client.gui.mapper

/**
 * Created by marco on 18.07.17.
 */
class Button : GuiElement() {
  protected val texture = ResourceLocation("textures/gui/widgets.png")

  var value: String by mapper()
  var enabled: Boolean by mapper()

  init {
    value = ""
    enabled = true
    width = 200
    height = 20
  }

  override fun render(mouseX: Int, mouseY: Int) {
    TODO("not implemented")
  }

}