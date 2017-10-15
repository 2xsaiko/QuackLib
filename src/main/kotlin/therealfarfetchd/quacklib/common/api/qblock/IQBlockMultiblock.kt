package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos

interface IQBlockMultiblock {
  private val qb: QBlock
    get() = this as QBlock

  /**
   * Returns the positions relative to this block where to place multiblock blocks.
   */
  fun getFillBlocks(): Set<BlockPos>

  fun onRemoteBreak(from: BlockPos) {
    if (!qb.canStay()) qb.dismantle()
  }

  fun onActivatedRemote(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, from: BlockPos, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return qb.onActivated(player, hand, facing, hitX, hitY, hitZ)
  }
}