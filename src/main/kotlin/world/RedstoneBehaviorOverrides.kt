package therealfarfetchd.quacklib.world

import net.minecraft.block.Block
import net.minecraft.block.BlockDirectional
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

private typealias ConnectionOp = (world: World, pos: BlockPos, state: IBlockState, side: EnumFacing) -> Boolean

object RedstoneBehaviorOverrides {

  private val connection: MutableMap<Block, ConnectionOp> = mutableMapOf()

  fun registerConnectionOverride(b: Block, op: ConnectionOp) {
    connection[b] = op
  }

  fun canConnectTo(world: World, pos: BlockPos, state: IBlockState, side: EnumFacing): Boolean {
    val b = state.block
    return connection[b]?.invoke(world, pos, state, side)
           ?: b.canConnectRedstone(state, world, pos, side)
  }

  init {
    registerConnectionOverride(Blocks.PISTON) { _, _, state, side -> state.getValue(BlockDirectional.FACING) != side }
  }

}