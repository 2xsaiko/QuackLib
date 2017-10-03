package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.IInteractionObject
import net.minecraftforge.items.wrapper.SidedInvWrapper

interface IQBlockInventory : ISidedInventory, IInteractionObject {
  val inventorySize: Int

  override fun getInventoryStackLimit(): Int = 64
  override fun getFieldCount(): Int = 0
  override fun hasCustomName(): Boolean = customName != null
  override fun getSizeInventory(): Int = inventorySize
  override fun getGuiID(): String = (this as QBlock).blockType.toString()

  override fun getName(): String {
    val type = (this as QBlock).blockType
    return "${type.resourceDomain}:container.${type.resourcePath}"
  }

  val customName: String?
    get() = null

  override fun getStackInSlot(index: Int): ItemStack = getStack(index)
  override fun setInventorySlotContents(index: Int, stack: ItemStack) = setStack(index, stack)
  override fun removeStackFromSlot(index: Int): ItemStack = removeStack(index)
  override fun getDisplayName(): ITextComponent =
    if (this.hasCustomName()) TextComponentString(customName) else TextComponentTranslation(name)

  fun getStack(index: Int): ItemStack
  fun setStack(index: Int, stack: ItemStack)

  fun removeStack(index: Int): ItemStack {
    val stack = getStack(index)
    setStack(index, ItemStack.EMPTY)
    return stack
  }

  override fun decrStackSize(index: Int, count: Int): ItemStack {
    val stack = getStack(index)
    val removable = minOf(count, stack.count)
    val ret = stack.copy().also { it.count = removable }
    stack.count -= removable
    setStack(index, stack.takeUnless { it.isEmpty } ?: ItemStack.EMPTY)
    return ret
  }

  override fun canInsertItem(index: Int, stack: ItemStack, side: EnumFacing): Boolean {
    if (index !in getSlotsForFace(side)) return false
    if (!isItemValidForSlot(index, stack)) return false
    return true
  }

  override fun canExtractItem(index: Int, stack: ItemStack, side: EnumFacing): Boolean {
    if (index !in getSlotsForFace(side)) return false
    if (!isItemValidForSlot(index, stack)) return false
    return true
  }

  override fun clear() {
    for (i in 0 until inventorySize) setStack(i, ItemStack.EMPTY)
  }

  override fun isEmpty(): Boolean = (0 until inventorySize).map(this::getStack).all { it.isEmpty }

  override fun createContainer(inventory: InventoryPlayer, player: EntityPlayer): Container
  override fun openInventory(player: EntityPlayer) {}
  override fun closeInventory(player: EntityPlayer) {}
  override fun setField(id: Int, value: Int) {}
  override fun getField(id: Int): Int = 0
  override fun isUsableByPlayer(player: EntityPlayer): Boolean = true
  override fun getSlotsForFace(side: EnumFacing): IntArray = (0 until inventorySize).toList().toIntArray()

  override fun markDirty() {}

  fun handler(side: EnumFacing) = SidedInvWrapper(this, side)
}