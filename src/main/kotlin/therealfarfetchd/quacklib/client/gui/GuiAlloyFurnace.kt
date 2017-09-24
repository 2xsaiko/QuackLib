package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.player.InventoryPlayer
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory
import therealfarfetchd.quacklib.common.block.ContainerAlloyFurnace

class GuiAlloyFurnace(playerInv: InventoryPlayer, inventory: IQBlockInventory) : GuiContainer(ContainerAlloyFurnace(playerInv, inventory)) {

  override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
    super.drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    renderHoveredToolTip(mouseX, mouseY)
  }

  override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
    // do nothing, for now
    // TODO
  }

}