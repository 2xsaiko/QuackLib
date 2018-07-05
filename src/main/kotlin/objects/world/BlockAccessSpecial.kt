package therealfarfetchd.quacklib.objects.world

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess

class BlockAccessSpecial(val source: IBlockAccess, var overridePos: BlockPos, var state: IBlockState, var tile: TileEntity?) : IBlockAccess by source {

  override fun getTileEntity(pos: BlockPos): TileEntity? =
    if (overridePos == pos) tile
    else source.getTileEntity(pos)

  override fun getBlockState(pos: BlockPos): IBlockState =
    if (overridePos == pos) state
    else source.getBlockState(pos)

  override fun isAirBlock(pos: BlockPos): Boolean =
    getBlockState(pos).let { it.block.isAir(it, this, pos) }

  override fun getStrongPower(pos: BlockPos, direction: EnumFacing): Int =
    getBlockState(pos).getStrongPower(this, pos, direction)

  override fun isSideSolid(pos: BlockPos, side: EnumFacing, _default: Boolean): Boolean =
    if (overridePos == pos) state.isSideSolid(this, pos, side)
    else source.isSideSolid(pos, side, _default)

}