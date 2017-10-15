package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import therealfarfetchd.quacklib.common.api.extensions.spawnAt
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

abstract class QBlockInventory : QBlock(), IQBlockInventory {
  protected val stacks = Array(sizeInventory, { ItemStack.EMPTY })

  override var customName: String? = null

  override fun getStack(index: Int): ItemStack = stacks[index]

  override fun setStack(index: Int, stack: ItemStack) {
    stacks[index] = stack
    dataChanged()
  }

  override fun onBreakBlock() {
    super.onBreakBlock()
    for (stack in stacks) stack.spawnAt(world, pos)
  }

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    customName?.also { nbt.string["CN"] = it }
    if (target != DataTarget.Client) {
      for ((i, item) in stacks.withIndex())
        item.writeToNBT(nbt.nbt["I$i"].self)
    }
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    if ("CN" in nbt) customName = nbt.string["CN"]
    if (target != DataTarget.Client) {
      for (i in stacks.indices)
        stacks[i] = ItemStack(nbt.nbt["I$i"].self)
    }
  }

  override fun isItemValidForSlot(index: Int, stack: ItemStack?): Boolean = true

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    return when (capability) {
      CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> if (side != null && getSlotsForFace(side).isNotEmpty()) handler(side) as T else null
      else -> super.getCapability(capability, side)
    }
  }
}