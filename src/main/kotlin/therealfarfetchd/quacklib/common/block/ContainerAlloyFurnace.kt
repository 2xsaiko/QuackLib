package therealfarfetchd.quacklib.common.block

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory

class ContainerAlloyFurnace(playerInv: InventoryPlayer, val inventory: IQBlockInventory) : Container() {
  var cookTime: Int = 0; private set
  var totalCookTime: Int = 0; private set
  var burnTime: Int = 0; private set
  var currentItemBurnTime: Int = 0; private set

  init {
    this.addSlotToContainer(SlotFurnaceFuel(inventory, 0, 17, 34))
    this.addSlotToContainer(SlotOutput(playerInv.player, inventory, 1, 134, 34))
    for (i in 0 until 3) {
      for (j in 0 until 3) {
        this.addSlotToContainer(Slot(inventory, 2 + j + i * 3, 44 + j * 18, 16 + i * 18))
      }
    }

    // Player inventory
    for (i in 0 until 3) {
      for (j in 0 until 9) {
        addSlotToContainer(Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18))
      }
    }

    for (i in 0 until 9) {
      addSlotToContainer(Slot(playerInv, i, 8 + i * 18, 142))
    }
  }

  override fun addListener(listener: IContainerListener) {
    super.addListener(listener)
    listener.sendAllWindowProperties(this, inventory)
  }

  /**
   * Looks for changes made in the container, sends them to every listener.
   */
  override fun detectAndSendChanges() {
    super.detectAndSendChanges()

    for (i in this.listeners.indices) {
      val icontainerlistener = this.listeners[i]

      if (this.burnTime != inventory.getField(0)) {
        icontainerlistener.sendWindowProperty(this, 0, inventory.getField(0))
      }

      if (this.currentItemBurnTime != inventory.getField(1)) {
        icontainerlistener.sendWindowProperty(this, 1, inventory.getField(1))
      }

      if (this.cookTime != inventory.getField(2)) {
        icontainerlistener.sendWindowProperty(this, 2, inventory.getField(2))
      }

      if (this.totalCookTime != inventory.getField(3)) {
        icontainerlistener.sendWindowProperty(this, 3, inventory.getField(3))
      }
    }

    this.burnTime = inventory.getField(0)
    this.currentItemBurnTime = inventory.getField(1)
    this.cookTime = inventory.getField(2)
    this.totalCookTime = inventory.getField(3)
  }

  @SideOnly(Side.CLIENT)
  override fun updateProgressBar(id: Int, data: Int) {
    inventory.setField(id, data)
    when (id) {
      0 -> burnTime = data
      1 -> currentItemBurnTime = data
      2 -> cookTime = data
      3 -> totalCookTime = data
    }
  }

  /**
   * Handle when the stack in slot `index` is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
    var itemstack = ItemStack.EMPTY
    val slot = this.inventorySlots[index] ?: return itemstack
    val stack = slot.stack
    if (!stack.isEmpty) {
      itemstack = stack.copy()

      if (index < 11) {
        if (!this.mergeItemStack(stack, 11, 46, true)) {
          return ItemStack.EMPTY
        }
      } else {
        if (!this.mergeItemStack(stack, 0, 1, false)) {
          if (!this.mergeItemStack(stack, 2, 11, false)) {
            return ItemStack.EMPTY
          }
        }
      }

      if (stack.isEmpty) {
        slot.putStack(ItemStack.EMPTY)
      } else {
        slot.onSlotChanged()
      }

      if (stack.count == itemstack.count) {
        return ItemStack.EMPTY
      }

      slot.onTake(playerIn, stack)
    }

    return itemstack
  }

  override fun canInteractWith(playerIn: EntityPlayer): Boolean = inventory.isUsableByPlayer(playerIn)

  class SlotOutput(
    player: EntityPlayer,
    inventoryIn: IInventory,
    slotIndex: Int,
    xPosition: Int,
    yPosition: Int
  ) : SlotFurnaceOutput(player, inventoryIn, slotIndex, xPosition, yPosition) {
    override fun onCrafting(stack: ItemStack?) {}
  }
}