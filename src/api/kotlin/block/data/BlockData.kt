package therealfarfetchd.quacklib.api.block.data

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

interface BlockDataRO {

  val world: IBlockAccess
  val pos: BlockPos
  val state: IBlockState

}

interface BlockData : BlockDataRO {

  override val world: World

}

operator fun BlockDataRO.component1() = world

operator fun BlockData.component1() = world

operator fun BlockDataRO.component2() = pos

operator fun BlockDataRO.component3() = state