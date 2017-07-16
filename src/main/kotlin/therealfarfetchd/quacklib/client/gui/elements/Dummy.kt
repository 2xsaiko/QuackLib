package therealfarfetchd.quacklib.client.gui.elements

import net.minecraft.client.renderer.GlStateManager.*
import org.lwjgl.opengl.GL11
import therealfarfetchd.quacklib.client.gui.GuiElement

/**
 * Created by marco on 16.07.17.
 */
class Dummy : GuiElement() {
  override fun render(mouseX: Int, mouseY: Int) {
    disableTexture2D()
    glLineWidth(1f)
    glBegin(GL11.GL_LINES)
    color(1f, 0f, 0f)
    glVertex3f(0f, 0f, 0f)
    val widthf = width.toFloat()
    val heightf = height.toFloat()
    glVertex3f(widthf, 0f, 0f)
    glVertex3f(widthf, 0f, 0f)
    glVertex3f(widthf, heightf, 0f)
    glVertex3f(widthf, heightf, 0f)
    glVertex3f(0f, heightf, 0f)
    glVertex3f(0f, heightf, 0f)
    glVertex3f(0f, 0f, 0f)
    glVertex3f(0f, 0f, 0f)
    glVertex3f(widthf, heightf, 0f)
    glEnd()
    color(1f, 1f, 1f)
  }
}