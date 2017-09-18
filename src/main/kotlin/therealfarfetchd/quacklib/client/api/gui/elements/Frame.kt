package therealfarfetchd.quacklib.client.api.gui.elements

import net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect
import net.minecraft.client.renderer.GlStateManager.color
import net.minecraft.client.renderer.GlStateManager.enableTexture2D
import therealfarfetchd.quacklib.client.api.gui.GuiElement
import therealfarfetchd.quacklib.client.api.gui.number

/**
 * Created by marco on 16.07.17.
 */
class Frame : GuiElement() {
  var r: Float by number()
  var g: Float by number()
  var b: Float by number()

  init {
    r = 1F
    g = 1F
    b = 1F
  }

  override fun render(mouseX: Int, mouseY: Int) {
    color(r, g, b)
    mc.textureManager.bindTexture(sprites)
    enableTexture2D()
    val width1 = width
    val height1 = height
    drawScaledCustomSizeModalRect(-4, -4, 0F, 0F, 4, 4, 4, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(width1, -4, 4F, 0F, 4, 4, 4, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(-4, height1, 0F, 4F, 4, 4, 4, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(width1, height1, 4F, 4F, 4, 4, 4, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(0, -4, 3F, 0F, 1, 4, width1, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(width1, 0, 4F, 3F, 4, 1, 4, height1, 128F, 128F)
    drawScaledCustomSizeModalRect(0, height1, 3F, 4F, 1, 4, width1, 4, 128F, 128F)
    drawScaledCustomSizeModalRect(-4, 0, 0F, 3F, 4, 1, 4, height1, 128F, 128F)
    drawScaledCustomSizeModalRect(0, 0, 3F, 3F, 1, 1, width1, height1, 128F, 128F)
  }
}