package therealfarfetchd.quacklib.common

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState

/**
 * Created by marco on 08.07.17.
 */
open class QBContainer(rl: ResourceLocation, private val factory: () -> QBlock) : Block(factory.also { tempFactory = it }().material), ITileEntityProvider {

  init {
    registryName = rl
    unlocalizedName = rl.toString()
  }

  private fun checkValid(world: IBlockAccess, pos: BlockPos) = world.getBlockState(pos).block == this && world.getTileEntity(pos) is QBContainerTile
  private fun requireValid(world: IBlockAccess, pos: BlockPos) = check(checkValid(world, pos))

  private fun tempQB(world: World, pos: BlockPos): QBlock = factory().also { it.world = world; it.pos = pos }

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
    val block = getQBlockAt(world, pos)
    return block.applyProperties(state)
  }

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    val block = getQBlockAt(world, pos)
    if (state is IExtendedBlockState) return block.applyExtendedProperties(state)
    else return state
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

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    if (checkValid(world, pos)) {
      val qb = getQBlockAt(world, pos)
      qb.onRemoved()
    }

    super.breakBlock(world, pos, state)
  }

  companion object {
    internal lateinit var tempFactory: () -> QBlock
  }

}