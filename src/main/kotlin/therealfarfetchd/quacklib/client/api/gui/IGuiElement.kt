package therealfarfetchd.quacklib.client.api.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import org.lwjgl.opengl.GL11
import therealfarfetchd.quacklib.common.api.util.ObservableMap

/**
 * Created by marco on 16.07.17.
 */
interface IGuiElement {

  val width: Int
  val height: Int

  var elements: Set<GuiElement>

  val properties: ObservableMap<String, Any?>

  val name: String?

  fun render(mouseX: Int, mouseY: Int) {
    GlStateManager.disableTexture2D()
    GlStateManager.glLineWidth(1f)
    GlStateManager.glBegin(GL11.GL_LINES)
    GlStateManager.color(1f, 0f, 0f)
    GlStateManager.glVertex3f(0f, 0f, 0f)
    val widthf = width.toFloat()
    val heightf = height.toFloat()
    GlStateManager.glVertex3f(widthf, 0f, 0f)
    GlStateManager.glVertex3f(widthf, 0f, 0f)
    GlStateManager.glVertex3f(widthf, heightf, 0f)
    GlStateManager.glVertex3f(widthf, heightf, 0f)
    GlStateManager.glVertex3f(0f, heightf, 0f)
    GlStateManager.glVertex3f(0f, heightf, 0f)
    GlStateManager.glVertex3f(0f, 0f, 0f)
    GlStateManager.glVertex3f(0f, 0f, 0f)
    GlStateManager.glVertex3f(widthf, heightf, 0f)
    GlStateManager.glEnd()
    GlStateManager.color(1f, 1f, 1f)
  }

  fun mouseClicked(x: Int, y: Int, button: Int) {
    elements.forEach { it.mouseClicked(x - it.getEffectiveX(it.x), y - it.getEffectiveY(it.y), button) }
  }

  fun mouseReleased(x: Int, y: Int, button: Int) {
    elements.forEach { it.mouseReleased(x - it.getEffectiveX(it.x), y - it.getEffectiveY(it.y), button) }
  }

  fun mouseDragged(x: Int, y: Int, button: Int, timeSinceLastClick: Long) {
    elements.forEach { it.mouseDragged(x - it.getEffectiveX(it.x), y - it.getEffectiveY(it.y), button, timeSinceLastClick) }
  }

  fun localized(str: String): String {
    var text = str
    if (str.startsWith("#") && I18n.hasKey(str.substring(1))) {
      text = I18n.format(str.substring(1))
    }
    return text
  }

}