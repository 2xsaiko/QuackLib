package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID

/**
 * Created by marco on 16.07.17.
 */
abstract class GuiElement : IGuiElement {
  internal val sprites = ResourceLocation("$ModID:textures/gui/elements.png")

  protected val mc = Minecraft.getMinecraft()

  var x: Int by number()
  var y: Int by number()
  override final var width: Int by number()
  override final var height: Int by number()
  var relx: RelativeX by transform<RelativeX, String>({ name.toLowerCase() }, { RelativeX.byName(this) })
  var rely: RelativeY by transform<RelativeY, String>({ name.toLowerCase() }, { RelativeY.byName(this) })

  internal lateinit var parent: IGuiElement
  override final var elements: Set<GuiElement> = emptySet()

  override final var properties: Map<String, Any?> = emptyMap()

  init {
    x = 0
    y = 0
    width = 20
    height = 20
    relx = RelativeX.Left
    rely = RelativeY.Top
  }

  open fun transformAndRender(mouseX: Int, mouseY: Int) {
    GlStateManager.pushMatrix()
    val effx = getEffectiveX(x)
    val effy = getEffectiveY(y)
    GlStateManager.translate(effx.toDouble(), effy.toDouble(), 1.0)
    GlStateManager.pushMatrix()
    render(mouseX - effx, mouseY - effy)
    GlStateManager.popMatrix()
    elements.forEach { it.transformAndRender(mouseX - effx, mouseY - effy) }
    GlStateManager.popMatrix()
  }

  open fun getEffectiveX(x: Int): Int {
    when (relx) {
      RelativeX.Left -> return x
      RelativeX.Center -> return x + parent.width / 2 - width / 2
      RelativeX.Right -> return x + parent.width - width
    }
  }

  open fun getEffectiveY(y: Int): Int {
    when (rely) {
      RelativeY.Top -> return y
      RelativeY.Center -> return y + parent.height / 2 - height / 2
      RelativeY.Bottom -> return y + parent.height - height
    }
  }

}