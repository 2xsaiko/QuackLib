package therealfarfetchd.quacklib.common

import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty

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
    get() = collisionBox

  /**
   * Returns the raytrace collision box of the block
   */
  open val rayCollisionBox: AxisAlignedBB
    get() = selectionBox

  /**
   * Returns the entity collision box of the block.
   */
  open val collisionBox: AxisAlignedBB = FullAABB

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
  val properties: Set<IProperty<*>> = emptySet()

  /**
   * A set of all the unlisted blockstate properties. Must stay constant at all times.
   */
  val unlistedProperties: Set<IUnlistedProperty<*>> = emptySet()

  /**
   * Called when the block gets added to the world.
   */
  open fun onAdded() {}

  /**
   * Called when the block gets removed from the world.
   */
  open fun onRemoved() {}

  /**
   * Destroys the block. Returns true if it was successful.
   */
  fun dismantle(dropItems: Boolean = true, force: Boolean = false): Boolean {
    val flag = onBreakBlock(null) || force
    if (flag) {
      if (dropItems) dropItems(null)
      world.setBlockToAir(pos)
    }
    return flag
  }

  /**
   * Drop the items in the world.
   */
  open fun dropItems(player: EntityPlayer?) {
    for (item in getDroppedItems(player)) {
      item.spawnAt(world, pos)
    }
  }

  /**
   * Called when the block gets broken. This does not actually remove the block from the world.
   * Returns true if the block is allowed to be removed from the world.
   */
  open fun onBreakBlock(player: EntityPlayer?): Boolean {
    return true
  }

  /**
   * Returns true if this is a valid position for the block
   */
  open fun canStay(): Boolean = true

  /**
   * Returns true if this block can be placed on the specified side.
   */
  open fun canBePlacedOnSide(side: EnumFacing): Boolean = canStay()

  /**
   * Gets called when one of the block's neighbors gets changed (broken, placed, …)
   */
  open fun onNeighborChanged(side: EnumFacing) {
    if (!canStay()) dismantle(force = true)
  }

  /**
   * Add the properties to the block state.
   */
  open fun applyProperties(state: IBlockState): IBlockState = state

  /**
   * Add the extended properties to the block state.
   */
  open fun applyExtendedProperties(state: IExtendedBlockState): IBlockState = state

  /**
   * Returns the items this block drops.
   */
  abstract fun getDroppedItems(player: EntityPlayer?): List<ItemStack>

  companion object {
    val FullAABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
  }

}