package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.player.EntityPlayer

import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.ILockableContainer
import net.minecraft.world.LockCode

open class QBContainerTileInventory() : QBContainerTile(), ILockableContainer, ISidedInventory {
  val qbinv: IQBlockInventory
    get() = qb as IQBlockInventory

  private var code: LockCode = LockCode.EMPTY_CODE

  override fun isLocked(): Boolean {
    return !this.code.isEmpty
  }

  override fun getLockCode(): LockCode {
    return this.code
  }

  override fun setLockCode(code: LockCode) {
    this.code = code
  }

  override fun getStackInSlot(index: Int): ItemStack {
    return qbinv.getStack(index)
  }

  override fun clear() {
    qbinv.clear()
  }

  override fun getName(): String {
    return qbinv.name
  }

  override fun getInventoryStackLimit(): Int {
    return qbinv.inventoryStackLimit
  }

  override fun createContainer(playerInventory: InventoryPlayer, playerIn: EntityPlayer): Container {
    return qbinv.createContainer(playerInventory, playerIn)
  }

  override fun openInventory(player: EntityPlayer) {
    qbinv.openInventory(player)
  }

  override fun closeInventory(player: EntityPlayer) {
    return qbinv.closeInventory(player)
  }

  override fun getGuiID(): String {
    return qbinv.guiID
  }

  override fun removeStackFromSlot(index: Int): ItemStack {
    return qbinv.removeStack(index)
  }

  override fun getFieldCount(): Int {
    return qbinv.fieldCount
  }

  override fun setField(id: Int, value: Int) {
    qbinv.setField(id, value)
  }

  override fun getField(id: Int): Int {
    return qbinv.getField(id)
  }

  override fun hasCustomName(): Boolean {
    return qbinv.hasCustomName()
  }

  override fun decrStackSize(index: Int, count: Int): ItemStack {
    return qbinv.decrStackSize(index, count)
  }

  override fun getSizeInventory(): Int {
    return qbinv.inventorySize
  }

  override fun isEmpty(): Boolean {
    return qbinv.isEmpty
  }

  override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
    return qbinv.isItemValidForSlot(index, stack)
  }

  override fun isUsableByPlayer(player: EntityPlayer): Boolean {
    return qbinv.isUsableByPlayer(player)
  }

  override fun setInventorySlotContents(index: Int, stack: ItemStack) {
    return qbinv.setStack(index, stack)
  }

  override fun getSlotsForFace(side: EnumFacing): IntArray {
    return qbinv.getSlotsForFace(side)
  }

  override fun canInsertItem(index: Int, itemStackIn: ItemStack, direction: EnumFacing): Boolean {
    return qbinv.canInsertItem(index, itemStackIn, direction)
  }

  override fun canExtractItem(index: Int, stack: ItemStack, direction: EnumFacing): Boolean {
    return qbinv.canExtractItem(index, stack, direction)
  }

  constructor(qbIn: QBlock) : this() {
    check(qbIn is IQBlockInventory) { "Illegal type: qbIn must be QBlock with IQBlockInventory" }
    qb = qbIn
  }

  open class Ticking() : QBContainerTileInventory(), ITickingQBTile {
    constructor(qbIn: QBlock) : this() {
      check(qbIn is IQBlockInventory) { "Illegal type: qbIn must be QBlock with IQBlockInventory" }
      qb = qbIn
    }
  }
}