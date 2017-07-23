package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import therealfarfetchd.quacklib.common.util.ObservableMap
import java.math.BigDecimal

/**
 * Created by marco on 16.07.17.
 */
class QGuiScreen(private val logic: AbstractGuiLogic) : GuiScreen() {

  val root = ScreenRoot()

  private var realScale: Float = 1.0F

  override fun doesGuiPauseGame(): Boolean {
    return root.pause
  }

  override fun initGui() {
    super.initGui()
    val res = ScaledResolution(mc)
    realScale = findScale(res.scaleFactor, root.scale) / res.scaleFactor.toFloat()
  }

  private fun findScale(mcScale: Int, validScales: List<Int>): Int {
    return validScales.filter { it <= mcScale }.max()
           ?: validScales.min()
           ?: findScale(mcScale, listOf(1, 2, 3, 4))
  }

  override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    logic.update()
    drawDefaultBackground()
    GlStateManager.pushMatrix()
    GlStateManager.scale(realScale, realScale, 1F)
    root.render((mouseX / realScale).toInt(), (mouseY / realScale).toInt())
    GlStateManager.popMatrix()
  }

  override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
    root.mouseClicked((mouseX / realScale).toInt(), (mouseY / realScale).toInt(), mouseButton)
  }

  override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
    root.mouseReleased(mouseX, mouseY, state)
  }

  override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
    root.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
  }

  inner class ScreenRoot : IGuiElement {
    override val name: String = "root"

    override val width: Int
      get() = (this@QGuiScreen.width / realScale).toInt()

    override val height: Int
      get() = (this@QGuiScreen.height / realScale).toInt()

    override var elements: Set<GuiElement> = emptySet()
    override val properties: ObservableMap<String, Any?> = ObservableMap()

    var scale: List<Int> by transform<List<Int>, List<BigDecimal>>({ map { BigDecimal(it) } }, { map { it.intValueExact() } })
    var pause: Boolean by mapper()

    init {
      scale = emptyList() // let MC handle the scaling
      pause = false
    }

    override fun render(mouseX: Int, mouseY: Int) {
      elements.forEach { it.transformAndRender(mouseX, mouseY) }
    }
  }

}