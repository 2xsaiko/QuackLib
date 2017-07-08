package therealfarfetchd.quacklib.common

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by marco on 08.07.17.
 */
open class QBContainer(rl: ResourceLocation, private val factory: () -> QBlock) : Block(factory().material), ITileEntityProvider {

  init {
    registryName = rl
    unlocalizedName = rl.toString()
  }

  fun checkIfValidAt(world: IBlockAccess, pos: BlockPos) = check(world.getBlockState(pos).block == this && world.getTileEntity(pos) is QBContainerTile)

  fun getQBlockAt(world: World, pos: BlockPos): QBlock {
    checkIfValidAt(world, pos)
    val te = world.getTileEntity(pos) as QBContainerTile
    return te.qb
  }

  override fun getBlockHardness(blockState: IBlockState?, world: World, pos: BlockPos): Float {
    return getQBlockAt(world, pos).hardness
  }

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = factory()
    if (qb is ITickable) return QBContainerTile.Ticking(qb)
    else return QBContainerTile(factory())
  }

}