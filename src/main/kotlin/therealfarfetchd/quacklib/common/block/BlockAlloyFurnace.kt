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
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.CapabilityItemHandler
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.isClient
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.extensions.spawnAt
import therealfarfetchd.quacklib.common.api.qblock.IQBlockInventory
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.qblock.WrapperImplManager
import therealfarfetchd.quacklib.common.api.recipe.AlloyFurnaceRecipes
import therealfarfetchd.quacklib.common.api.recipe.ItemTemplate
import therealfarfetchd.quacklib.common.api.util.ChangeListener
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

class BlockAlloyFurnace : QBlock(), IQBlockInventory, ITickable {
  private val stacks: Array<ItemStack> = Array(11, { ItemStack.EMPTY })
  private val fuelSlot get() = stacks[0]
  private var resultSlot
    get() = stacks[1]
    set(value) {
      stacks[1] = value
    }
  private val inputSlots get() = stacks.slice(2..10)

  private var facing: EnumFacing = EnumFacing.NORTH

  private var burnTime: Int = 0
  private var cookTime: Int = 0
  private var totalCookTime: Int = 0
  private var currentItemBurnTime: Int = 0

  override var customName: String? = null

  private val clientCL = ChangeListener(this::customName)
  private val displayCL = ChangeListener(this::facing, this::currentItemBurnTime)
  private val worldCL = ChangeListener(this::stacks, this::facing, this::burnTime, this::cookTime, this::totalCookTime, this::currentItemBurnTime, this::customName)

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

  override fun update() {
    if (world.isClient) return

    val recipe = AlloyFurnaceRecipes.findRecipe(inputSlots)
      ?.let { it.first to it.second.makeStack() }
      ?.takeIf { resultSlot.isEmpty || resultSlot.isItemEqual(it.second) }
    val hasRecipe = recipe != null

    if (burnTime < currentItemBurnTime) {
      burnTime++
    } else {
      burnTime = 0
      currentItemBurnTime = 0
      if (hasRecipe) takeFuel()
    }

    if (hasRecipe && currentItemBurnTime != 0) {
      if (cookTime < totalCookTime) {
        cookTime++
      } else {
        if (totalCookTime != 0) craftRecipe(recipe!!)
        cookTime = 0
        totalCookTime = 200
      }
    } else {
      cookTime = 0
      totalCookTime = 0
    }


    if (worldCL.valuesChanged()) dataChanged()
    if (displayCL.valuesChanged()) {
      clientDataChanged(true)
      clientCL.valuesChanged() // clear clientCL changed flag
    } else if (clientCL.valuesChanged()) {
      clientDataChanged(false)
    }
  }

  private fun craftRecipe(r: Pair<List<ItemTemplate>, ItemStack>) {
    val outStack = r.second

    if (resultSlot.isEmpty || resultSlot.isItemEqual(outStack)) {
      for (item in r.first) {
        var count = item.makeStack().count
        for (invItem in inputSlots) {
          if (item.isSameItem(invItem)) {
            val take = minOf(invItem.count, count)
            invItem.count -= take
            count -= take
          }
        }
      }
      if (resultSlot.isEmpty) resultSlot = outStack
      else resultSlot.also { it.count += outStack.count }
    }

  }

  override fun getField(id: Int): Int {
    return when (id) {
      0 -> burnTime
      1 -> currentItemBurnTime
      2 -> cookTime
      3 -> totalCookTime
      else -> error("Out of range")
    }
  }

  override fun setField(id: Int, value: Int) {
    when (id) {
      0 -> burnTime = value
    // 1 -> currentItemBurnTime = value
      2 -> cookTime = value
      3 -> totalCookTime = value
    }
  }

  private fun getBurnTime(stack: ItemStack) = TileEntityFurnace.getItemBurnTime(stack)

  private fun takeFuel(): Boolean {
    val burnTime = getBurnTime(fuelSlot)
    return if (burnTime > 0) {
      fuelSlot.count--
      currentItemBurnTime += burnTime
      true
    } else false
  }

  override fun createContainer(inventory: InventoryPlayer, player: EntityPlayer): Container {
    return ContainerAlloyFurnace(inventory, this)
  }

  override fun rotateBlock(axis: EnumFacing): Boolean {
    facing = facing.rotateY()
    return true
  }

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    nbt.ubyte["F"] = facing.horizontalIndex
    nbt.ushort["MB"] = currentItemBurnTime
    customName?.also { nbt.string["CN"] = it }
    if (target != DataTarget.Client) {
      nbt.ushort["B"] = burnTime
      nbt.ushort["C"] = cookTime
      nbt.ushort["MC"] = totalCookTime
      for ((i, item) in stacks.withIndex())
        item.writeToNBT(nbt.nbt["I$i"].self)
    }
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    facing = EnumFacing.getHorizontal(nbt.ubyte["F"])
    currentItemBurnTime = nbt.ushort["MB"]
    if ("CN" in nbt) customName = nbt.string["CN"]
    if (target != DataTarget.Client) {
      burnTime = nbt.ushort["B"]
      cookTime = nbt.ushort["C"]
      totalCookTime = nbt.ushort["MC"]
      for (i in stacks.indices)
        stacks[i] = ItemStack(nbt.nbt["I$i"].self)
    }
  }

  override fun applyProperties(state: IBlockState): IBlockState {
    return super.applyProperties(state)
      .withProperty(PropFacing, facing)
      .withProperty(PropLit, currentItemBurnTime != 0)
  }

  override fun getSlotsForFace(side: EnumFacing): IntArray {
    return when (side) {
      EnumFacing.DOWN -> intArrayOf(0) // fuel slot
      EnumFacing.UP -> (2..10).toList().toIntArray() // input slots
      EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST -> intArrayOf(1) // output slot
    }
  }

  override fun onBreakBlock() {
    for (stack in stacks) stack.spawnAt(world, pos)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    return when (capability) {
      CapabilityItemHandler.ITEM_HANDLER_CAPABILITY -> if (side != null) handler(side) as T else null
      else -> super.getCapability(capability, side)
    }
  }

  override fun getItem(): ItemStack = Item.makeStack()
  override fun getFieldCount(): Int = 4

  override val properties: Set<IProperty<*>> = super.properties + PropFacing + PropLit
  override val material: Material = Material.ROCK
  override val blockType: ResourceLocation = ResourceLocation(ModID, "alloy_furnace")
  override val inventorySize: Int = 11

  companion object {
    val PropFacing = PropertyEnum.create("facing", EnumFacing::class.java, *EnumFacing.HORIZONTALS)
    val PropLit = PropertyBool.create("lit")

    val Block = WrapperImplManager.getContainer(BlockAlloyFurnace::class)
    val Item = WrapperImplManager.getItem(BlockAlloyFurnace::class)
  }
}