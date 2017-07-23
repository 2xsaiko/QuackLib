package therealfarfetchd.quacklib.client.gui.elements

import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.SoundEvents
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.client.gui.GuiElement
import therealfarfetchd.quacklib.client.gui.mapper

/**
 * Created by marco on 18.07.17.
 */
open class Button : GuiElement() {
  protected val texture = ResourceLocation("textures/gui/widgets.png")

  var value: String by mapper()
  protected var enabled: Boolean by mapper()

  protected var clicked: Boolean = false

  init {
    value = ""
    enabled = true
    width = 200
    height = 20
  }

  override fun render(mouseX: Int, mouseY: Int) {
    GlStateManager.enableTexture2D()

    val text = localized(value)
    val g = Gui()
    val fontrenderer = mc.fontRenderer
    mc.textureManager.bindTexture(texture)
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    val hovered = mouseX in 0..width && mouseY in 0..height
    val i = if (enabled) {
      if (hovered) 2 else 1
    } else 0

    GlStateManager.enableBlend()
    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO)
    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)
    val partWidth = width / 2
    val partHeight = height / 2
    val partHeight2 = partHeight + height % 2
    val u1 = 200 - partWidth
    val v1 = 46 + i * 20
    val v2 = 56 + i * 20 + (20 - height) / 2
    g.drawTexturedModalRect(0, 0, 0, v1, partWidth, partHeight)
    g.drawTexturedModalRect(partWidth, 0, u1, v1, partWidth, partHeight)
    g.drawTexturedModalRect(0, partHeight, 0, v2, partWidth, partHeight2)
    g.drawTexturedModalRect(partWidth, partHeight, u1, v2, partWidth, partHeight2)
    var j = 0xE0E0E0

    if (!enabled) {
      j = 0xA0A0A0
    } else if (hovered) {
      j = 0xFFFFA0
    }

    g.drawCenteredString(fontrenderer, text, partWidth, (height - 8) / 2, j)
  }

  open fun buttonClick(button: Int) {
    mc.soundHandler.playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f))
    fireEvent()
  }

  override fun mouseClicked(x: Int, y: Int, button: Int) {
    super.mouseClicked(x, y, button)
    if (x in 0..width && y in 0..height) clicked = true
  }

  override fun mouseReleased(x: Int, y: Int, button: Int) {
    super.mouseReleased(x, y, button)
    if (x in 0..width && y in 0..height && clicked) {
      clicked = false
      if (enabled) buttonClick(button)
    }
  }

}