package therealfarfetchd.quacklib.common.block

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory

class ContainerAlloyFurnace(val playerInv: InventoryPlayer, val inventory: IQBlockInventory) : Container() {
  private var cookTime: Int = 0
  private var totalCookTime: Int = 0
  private var furnaceBurnTime: Int = 0
  private var currentItemBurnTime: Int = 0

  init {
    for (i in 0 until 3) {
      for (j in 0 until 3) {
        this.addSlotToContainer(Slot(inventory, 2 + j + i * 3, 44 + j * 18, 15 + i * 18))
      }
    }
    this.addSlotToContainer(SlotFurnaceFuel(inventory, 0, 18, 51))
    this.addSlotToContainer(SlotFurnaceOutput(playerInv.player, inventory, 1, 146, 33))

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

      if (this.cookTime != inventory.getField(2)) {
        icontainerlistener.sendWindowProperty(this, 2, inventory.getField(2))
      }

      if (this.furnaceBurnTime != inventory.getField(0)) {
        icontainerlistener.sendWindowProperty(this, 0, inventory.getField(0))
      }

      if (this.currentItemBurnTime != inventory.getField(1)) {
        icontainerlistener.sendWindowProperty(this, 1, inventory.getField(1))
      }

      if (this.totalCookTime != inventory.getField(3)) {
        icontainerlistener.sendWindowProperty(this, 3, inventory.getField(3))
      }
    }

    this.cookTime = inventory.getField(2)
    this.furnaceBurnTime = inventory.getField(0)
    this.currentItemBurnTime = inventory.getField(1)
    this.totalCookTime = inventory.getField(3)
  }

  @SideOnly(Side.CLIENT)
  override fun updateProgressBar(id: Int, data: Int) {
    inventory.setField(id, data)
  }

  /**
   * Handle when the stack in slot `index` is shift-clicked. Normally this moves the stack between the player
   * inventory and the other inventory(s).
   */
  override fun transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack {
    var itemstack = ItemStack.EMPTY
    //    val slot = this.inventorySlots[index]
    //
    //    if (slot != null && slot.hasStack) {
    //      val itemstack1 = slot.stack
    //      itemstack = itemstack1.copy()
    //
    //      if (index == 2) {
    //        if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
    //          return ItemStack.EMPTY
    //        }
    //
    //        slot.onSlotChange(itemstack1, itemstack)
    //      } else if (index != 1 && index != 0) {
    //        if (!FurnaceRecipes.instance().getSmeltingResult(itemstack1).isEmpty) {
    //          if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
    //            return ItemStack.EMPTY
    //          }
    //        } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
    //          if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
    //            return ItemStack.EMPTY
    //          }
    //        } else if (index in 3..29) {
    //          if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
    //            return ItemStack.EMPTY
    //          }
    //        } else if (index in 30..38 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
    //          return ItemStack.EMPTY
    //        }
    //      } else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
    //        return ItemStack.EMPTY
    //      }
    //
    //      if (itemstack1.isEmpty) {
    //        slot.putStack(ItemStack.EMPTY)
    //      } else {
    //        slot.onSlotChanged()
    //      }
    //
    //      if (itemstack1.count == itemstack.count) {
    //        return ItemStack.EMPTY
    //      }
    //
    //      slot.onTake(playerIn, itemstack1)
    //    }

    return itemstack
  }

  override fun canInteractWith(playerIn: EntityPlayer): Boolean = inventory.isUsableByPlayer(playerIn)
}