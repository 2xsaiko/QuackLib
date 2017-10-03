package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory
import therealfarfetchd.quacklib.common.block.ContainerAlloyFurnace

class GuiAlloyFurnace(val playerInv: InventoryPlayer, val inventory: IQBlockInventory) : GuiContainer(ContainerAlloyFurnace(playerInv, inventory)) {

  val container: ContainerAlloyFurnace = inventorySlots as ContainerAlloyFurnace

  val texture = ResourceLocation(ModID, "textures/gui/alloy_furnace.png")

  override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    renderHoveredToolTip(mouseX, mouseY)
  }

  override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
    val s = inventory.displayName.unformattedText
    this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752)
    this.fontRenderer.drawString(playerInv.displayName.unformattedText, 8, this.ySize - 96 + 2, 4210752)
  }

  override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    GlStateManager.color(1f, 1f, 1f)
    Minecraft.getMinecraft().textureManager.bindTexture(texture)
    val windowX = (this.width - this.xSize) / 2
    val windowY = (this.height - this.ySize) / 2

    drawTexturedModalRect(windowX, windowY, 0, 0, xSize, ySize)

    this.getBurn(13).takeUnless { it == 0 }
      ?.also { drawTexturedModalRect(windowX + 18, windowY + 31 - it, 176, 13 - it, 14, it + 1) }

    this.getProgress(22).takeUnless { it == 0 }
      ?.also { drawTexturedModalRect(windowX + 102, windowY + 34, 176, 14, it, 16) }

    GlStateManager.enableLighting()
  }

  private fun getBurn(height: Int): Int = getScaled(container.currentItemBurnTime, container.currentItemBurnTime - container.burnTime, height)

  private fun getProgress(width: Int): Int = getScaled(container.totalCookTime, container.cookTime, width)

  private fun getScaled(base: Int, progress: Int, size: Int): Int {
    if (base == 0) return 0
    return (maxOf(0F, minOf(progress / base.toFloat(), 1F)) * size).toInt()
  }

}