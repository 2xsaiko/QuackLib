package therealfarfetchd.quacklib.common.block

import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.init.Items
import net.minecraft.inventory.Container
import net.minecraft.inventory.SlotFurnaceFuel
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.qblock.WrapperImplManager
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

class BlockAlloyFurnace : QBlock(), IQBlockInventory {
  private val stacks: Array<ItemStack> = Array(11, { ItemStack.EMPTY })
  private var facing: EnumFacing = EnumFacing.NORTH

  private var burnTime: Int = 0
  private var cookTime: Int = 0
  private var totalCookTime: Int = 0

  override var customName: String? = null

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    if (placer != null) facing = placer.adjustedHorizontalFacing.opposite
  }

  override fun getStack(index: Int): ItemStack = stacks[index]

  override fun setStack(index: Int, stack: ItemStack) {
    stacks[index] = stack
  }

  override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean {
    return when (index) {
      0 -> {
        val itemstack = stacks[1]
        TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && itemstack.item != Items.BUCKET
      }
      1 -> false
      else -> true
    }
  }

  override fun createContainer(inventory: InventoryPlayer, player: EntityPlayer): Container {
    return ContainerAlloyFurnace(inventory, this)
  }

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    nbt.ubyte["F"] = facing.horizontalIndex
    nbt.ushort["BT"] = burnTime
    nbt.ushort["CT"] = cookTime
    nbt.ushort["TCT"] = totalCookTime
    for ((i, item) in stacks.withIndex())
      item.writeToNBT(nbt.nbt["I$i"].self)
    customName?.also { nbt.string["CN"] = it }
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    facing = EnumFacing.getHorizontal(nbt.ubyte["F"])
    burnTime = nbt.ushort["BT"]
    cookTime = nbt.ushort["CT"]
    totalCookTime = nbt.ushort["TCT"]
    for (i in stacks.indices)
      stacks[i] = ItemStack(nbt.nbt["I$i"].self)
    if ("CN" in nbt) customName = nbt.string["CN"]
  }

  override fun applyProperties(state: IBlockState): IBlockState {
    return super.applyProperties(state).withProperty(PropFacing, facing)
  }

  override val properties: Set<IProperty<*>> = super.properties + PropFacing + PropLit
  override val material: Material = Material.ROCK
  override val blockType: ResourceLocation = ResourceLocation(ModID, "alloy_furnace")
  override val inventorySize: Int = 11

  override fun getFieldCount(): Int = 4

  override fun getItem(): ItemStack = Item.makeStack()

  companion object {
    val PropFacing = PropertyEnum.create("facing", EnumFacing::class.java, *EnumFacing.HORIZONTALS)
    val PropLit = PropertyBool.create("lit")

    val Block = WrapperImplManager.getContainer(BlockAlloyFurnace::class)
    val Item = WrapperImplManager.getItem(BlockAlloyFurnace::class)
  }
}