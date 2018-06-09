package therealfarfetchd.quacklib.block.data

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

interface BlockData {

  val pos: BlockPos
  val state: IBlockState
  val te: TileEntity

}