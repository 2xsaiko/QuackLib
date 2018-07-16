package therealfarfetchd.quacklib.notification

import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.toasts.GuiToast
import net.minecraft.client.gui.toasts.IToast
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

class NotificationQuackLib(val title: String, val body: String?, val expireTime: Long, val icon: ResourceLocation?) : IToast {

  private var firstDrawTime: Long? = null

  override fun draw(toastGui: GuiToast, delta: Long): IToast.Visibility {
    if (firstDrawTime == null) firstDrawTime = delta

    val style = 3

    val tm = toastGui.minecraft.textureManager
    tm.bindTexture(IToast.TEXTURE_TOASTS)
    GlStateManager.color(1.0f, 1.0f, 1.0f)
    toastGui.drawTexturedModalRect(0, 0, 0, 32 * style, 160, 32)

    val xBegin = if (icon == null) 7 else 18 + 7 + 4

    val fr = toastGui.minecraft.fontRenderer

    val titleS = shorten(title, fr, 160 - xBegin - 7)

    val titleColor = 0x00505050
    val bodyColor = 0x00707070

    if (icon != null) {
      tm.bindTexture(icon)
      Gui.drawScaledCustomSizeModalRect(7, 7, 0f, 0f, 16, 16, 18, 18, 16f, 16f)
    }

    if (this.body == null) {
      fr.drawString(titleS, xBegin, 12, titleColor)
    } else {
      val bodyS = shorten(body, fr, 160 - xBegin - 7)

      fr.drawString(titleS, xBegin, 7, titleColor)
      fr.drawString(bodyS, xBegin, 18, bodyColor)
    }

    return if (delta - this.firstDrawTime!! < expireTime) IToast.Visibility.SHOW else IToast.Visibility.HIDE
  }

  private fun shorten(s: String, fr: FontRenderer, max: Int): String {
    return if (fr.getStringWidth(s) > max) {
      val postfix = "..."
      var result = ""
      for (c in s) {
        if (fr.getStringWidth(result + c + postfix) > max) break
        result += c
      }
      result + postfix
    } else s
  }
}