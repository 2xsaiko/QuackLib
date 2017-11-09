package therealfarfetchd.quacklib.client.api.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.util.ObservableMap

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

  var parent: IGuiElement? = null
    internal set

  override var name: String? = null

  override final var elements: Set<GuiElement> = emptySet()

  override final val properties: ObservableMap<String, Any?> = ObservableMap()

  var action: GuiElement.() -> Any? = {}

  init {
    x = 0
    y = 0
    width = 20
    height = 20
    relx = RelativeX.Left
    rely = RelativeY.Top
  }

  protected fun fireEvent() {
    action(this)
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

  open fun getEffectiveX(x: Int) = when (relx) {
    RelativeX.Left -> x
    RelativeX.Center -> x + parent!!.width / 2 - width / 2
    RelativeX.Right -> x + parent!!.width - width
  }

  open fun getEffectiveY(y: Int) = when (rely) {
    RelativeY.Top -> y
    RelativeY.Center -> y + parent!!.height / 2 - height / 2
    RelativeY.Bottom -> y + parent!!.height - height
  }

}