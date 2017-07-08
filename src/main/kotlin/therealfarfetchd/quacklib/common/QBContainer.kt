package therealfarfetchd.quacklib.common

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import java.util.*

/**
 * Created by marco on 08.07.17.
 */
open class QBContainer(rl: ResourceLocation, private val factory: () -> QBlock) : Block(factory.also { tempFactory = it }().material), ITileEntityProvider {

  init {
    registryName = rl
    unlocalizedName = rl.toString()
  }

  private fun checkValid(world: IBlockAccess, pos: BlockPos) = world.getBlockState(pos).block == this && world.getTileEntity(pos) is QBContainerTile

  private fun requireValid(world: IBlockAccess, pos: BlockPos) {
    check(world.getBlockState(pos).block == this, { "Block state at $pos is not a QBContainer!" })
    check(world.getTileEntity(pos) != null, { "There is no tile entity at $pos!" })
    check(world.getTileEntity(pos) is QBContainerTile, { "Tile entity at $pos is not a QBContainerTile!" })
  }

  private fun tempQB(world: World?, pos: BlockPos?): QBlock = factory().also { qb -> world?.also { qb.world = it }; pos?.also { qb.pos = it } }

  fun getQBlockAt(world: IBlockAccess, pos: BlockPos): QBlock {
    requireValid(world, pos)
    val te = world.getTileEntity(pos) as QBContainerTile
    return te.qb
  }

  override fun createBlockState(): BlockStateContainer {
    @Suppress("USELESS_ELVIS")
    val qb = (factory ?: tempFactory)()
    return ExtendedBlockState(this, qb.properties.toTypedArray(), qb.unlistedProperties.toTypedArray())
  }

  override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    try {
      val block = getQBlockAt(world, pos)
      return block.applyProperties(state)
    } catch(e: IllegalStateException) {
      // probably client is still loading
      return state
    }
  }

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    try {
      val block = getQBlockAt(world, pos)
      if (state is IExtendedBlockState) return block.applyExtendedProperties(state)
    } catch(e: IllegalStateException) {
      // probably client is still loading, nop
    }
    return state

  }

  override fun getMetaFromState(state: IBlockState?): Int = 0
  override fun getStateFromMeta(meta: Int): IBlockState = defaultState

  override fun getBlockHardness(blockState: IBlockState?, world: World, pos: BlockPos): Float {
    return getQBlockAt(world, pos).hardness
  }

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = factory()
    if (qb is ITickable) return QBContainerTile.Ticking(qb)
    else return QBContainerTile(qb)
  }

  override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean = tempQB(world, pos).canStay()
  override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean = tempQB(world, pos).canBePlacedOnSide(side)

  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    val qb = (world.getTileEntity(pos) as QBContainerTile).qb
    qb.dropItems()
    super.breakBlock(world, pos, state)
  }

  override fun getSelectedBoundingBox(state: IBlockState?, world: World, pos: BlockPos): AxisAlignedBB = getQBlockAt(world, pos).selectionBox + pos

  override fun getCollisionBoundingBox(blockState: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    return try {
      getQBlockAt(world, pos)
    } catch(e: IllegalStateException) {
      tempQB(world as World, pos)
    }.rayCollisionBox
  }

  override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState?) {
    Scheduler.schedule(1) {
      getQBlockAt(world, pos).onAdded()
    }
  }

  override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ)
  }

  override fun getBoundingBox(state: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB = getQBlockAt(world, pos).collisionBox

  companion object {
    internal lateinit var tempFactory: () -> QBlock
  }

}