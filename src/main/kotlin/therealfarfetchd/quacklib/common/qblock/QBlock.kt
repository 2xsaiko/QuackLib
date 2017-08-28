package therealfarfetchd.quacklib.common.qblock

import net.minecraft.block.material.Material

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.common.DataTarget
import therealfarfetchd.quacklib.common.extensions.isServer
import therealfarfetchd.quacklib.common.extensions.spawnAt
import therealfarfetchd.quacklib.common.util.QNBTCompound

/**
 * Created by marco on 08.07.17.
 */
abstract class QBlock {

  /**
   * The block material. Must stay constant at all times.
   */
  abstract val material: Material

  /**
   * How long it takes to break this block.
   */
  open val hardness: Float = 1.0f

  /**
   * Returns the mouseover selection box of the block.
   */
  open val selectionBox: AxisAlignedBB
    get() = collisionBox ?: FullAABB

  /**
   * Returns the raytrace collision box of the block
   */
  open val rayCollisionBox: AxisAlignedBB?
    get() = selectionBox

  /**
   * Returns the entity collision box of the block.
   */
  open val collisionBox: AxisAlignedBB? = FullAABB

  /**
   * Returns true if this block is a full (0,0,0->1,1,1) cube. Must stay constant at all times.
   */
  open val isFullBlock = true

  /**
   * Returns true if this block is opaque (no transparent textures or full block). Must stay constant at all times.
   */
  @Suppress("LeakingThis")
  open val isOpaque: Boolean = isFullBlock

  /**
   * The tile entity this QBlock is in.
   */
  lateinit var container: QBContainerTile

  /**
   * The world this QBlock is in.
   */
  lateinit var world: World

  /**
   * The position this QBlock is at.
   */
  lateinit var pos: BlockPos

  /**
   * A set of all the blockstate properties. Must stay constant at all times.
   */
  open val properties: Set<IProperty<*>> = emptySet()

  /**
   * A set of all the unlisted blockstate properties. Must stay constant at all times.
   */
  open val unlistedProperties: Set<IUnlistedProperty<*>> = emptySet()

  /**
   * True if this block is not yet placed.
   */
  var prePlaced: Boolean = false

  /**
   * Called when the block gets added to the world.
   */
  open fun onAdded() {}

  /**
   * Called when the block is loaded.
   */
  open fun onLoad() {}

  /**
   * Called when the block is unloaded.
   */
  open fun onUnload() {}

  /**
   * Destroys the block.
   */
  fun dismantle(dropItems: Boolean = true) {
    if (world.isServer) {
      onBreakBlock()
      if (dropItems) dropItems()
      world.setBlockToAir(pos)
    }
  }

  /**
   * Drop the items in the world.
   */
  open fun dropItems() {
    for (item in getDroppedItems()) {
      item.spawnAt(world, pos)
    }
  }

  /**
   * Called when the block gets broken. This does not actually remove the block from the world.
   */
  open fun onBreakBlock() {}

  /**
   * Returns true if this is a valid position for the block
   */
  open fun canStay(): Boolean = true

  /**
   * Returns true if this block can be placed on the specified side.
   */
  open fun canBePlacedOnSide(side: EnumFacing): Boolean = canStay()

  /**
   * Gets called after the block is placed. Maybe. (only when placed by a player)
   * Also gets called if the block was placed as a multipart.
   */
  open fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {}

  /**
   * Gets called when the block gets rotated. (clicked on by a wrench)
   */
  open fun rotateBlock(axis: EnumFacing): Boolean = false

  /**
   * Gets called when one of the block's neighbors gets changed (broken, placed, …)
   */
  open fun onNeighborChanged(side: EnumFacing) {
    if (!canStay()) dismantle()
  }

  /**
   * Gets called when one of the block's neighbor TEs changes
   */
  open fun onNeighborTEChanged(side: EnumFacing) {}

  /**
   * Add the properties to the block state.
   */
  open fun applyProperties(state: IBlockState): IBlockState = state

  /**
   * Add the extended properties to the block state.
   */
  open fun applyExtendedProperties(state: IExtendedBlockState): IExtendedBlockState = state

  /**
   * Gets called when the block is clicked on. Returns true if the player should swing their hand.
   */
  open fun onActivated(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = false

  open fun getBlockFaceShape(facing: EnumFacing): BlockFaceShape = BlockFaceShape.CENTER

  /**
   * Determines the strength of this QBlock (how long it will take to break)
   */
  open fun blockStrength(player: EntityPlayer): Float {
    if (hardness < 0.0f) {
      return 0.0f
    }

    val state = world.getBlockState(pos)

    return if (!canHarvestBlock(player, world, pos)) {
      player.getDigSpeed(state, pos) / hardness / 100f
    } else {
      player.getDigSpeed(state, pos) / hardness / 30f
    }
  }

  open fun canHarvestBlock(player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Boolean {
    var state = world.getBlockState(pos)
    state = state.block.getActualState(state, world, pos)
    if (material.isToolNotRequired) {
      return true
    }

    val stack = player.heldItemMainhand
    val tool = container.blockType.getHarvestTool(this.world.getBlockState(this.pos))
    if (stack.isEmpty || tool == null) {
      return player.canHarvestBlock(state)
    }

    val toolLevel = stack.item.getHarvestLevel(stack, tool, player, state)
    return if (toolLevel < 0) {
      player.canHarvestBlock(state)
    } else toolLevel >= container.blockType.getHarvestLevel(this.world.getBlockState(this.pos))
  }

  /**
   * Returns the items this block drops.
   */
  open fun getDroppedItems(): List<ItemStack> = listOf(getItem())

  abstract fun getItem(): ItemStack

  /**
   * Gets called to save TE data to disk or to send to the client.
   */
  open fun saveData(nbt: QNBTCompound, target: DataTarget) {}

  /**
   * Gets called to load TE data.
   */
  open fun loadData(nbt: QNBTCompound, target: DataTarget) {}

  open fun validate() {}

  /**
   * Schedules the block for saving to disk.
   */
  fun dataChanged() {
    if (world.isServer) {
      world.markChunkDirty(pos, container)
    }
  }

  /**
   * Schedules the block for updating on the client side.
   */
  fun clientDataChanged(renderUpdate: Boolean = true) {
    if (world.isServer) {
      container.nextClientUpdateIsRender = renderUpdate || container.nextClientUpdateIsRender
      val state = applyProperties(container.blockType.defaultState)
      world.notifyBlockUpdate(pos, state, state, 3)
    } else {
      if (renderUpdate) {
        world.markBlockRangeForRenderUpdate(pos, pos)
      }
    }
  }

  open fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? = null

  companion object {
    val FullAABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
  }
}