package therealfarfetchd.quacklib.api.block.data

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface BlockData {

  val world: World
  val pos: BlockPos
  val state: IBlockState

}

operator fun BlockData.component1() = world

operator fun BlockData.component2() = pos

operator fun BlockData.component3() = state