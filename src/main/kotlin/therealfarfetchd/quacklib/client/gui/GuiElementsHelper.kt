package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID

// disabled color: 8b8b8b
// enabled color: ffffff
// shadow color: 686868

private val texture = ResourceLocation(ModID, "textures/gui/elements.png")

fun Gui.bindTexture(rl: ResourceLocation) = Minecraft.getMinecraft().textureManager.bindTexture(rl)

fun Gui.drawArrowRight(x: Int, y: Int, progress: Int) {
  bindTexture(texture)
  val widthT = getScaled(100, progress, 22)
  GlStateManager.color(0.55f, 0.55f, 0.55f, 1.0f)
  drawTexturedModalRect(x, y, 0, 8, 22, 15)
  if (widthT > 0) {
    GlStateManager.color(0.41f, 0.41f, 0.41f, 1.0f)
    drawTexturedModalRect(x, y + 1, 0, 8, widthT, 15)
    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
    drawTexturedModalRect(x, y, 0, 8, widthT, 15)
  }
  GlStateManager.color(1.0f, 1.0f, 1.0f)
}

fun Gui.drawArrowLeft(x: Int, y: Int, progress: Int) {
  bindTexture(texture)
  GlStateManager.pushMatrix()
  GlStateManager.translate(x + 11f, 0f, 0f)
  GlStateManager.scale(-1f, 1f, 1f)
  GlStateManager.disableCull()
  drawArrowRight(-11, y, progress)
  GlStateManager.enableCull()
  GlStateManager.popMatrix()
}

fun Gui.drawArrowRight(x: Int, y: Int, on: Boolean) = drawArrowRight(x, y, if (on) 100 else 0)

fun Gui.drawArrowLeft(x: Int, y: Int, on: Boolean) = drawArrowLeft(x, y, if (on) 100 else 0)

fun Gui.getScaled(base: Int, progress: Int, size: Int): Int {
  if (base == 0) return 0
  return (maxOf(0F, minOf(progress / base.toFloat(), 1F)) * size).toInt()
}