package therealfarfetchd.quacklib.common.api

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 11.07.17.
 */
interface IBlockWrenchable {
  /**
   * Gets called when the block is hit by a wrench.
   * Use only if the Block.rotateBlock(World, BlockPos, EnumFacing) function is not enough, for compatibility!
   */
  fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing, player: EntityPlayer?, hitX: Float, hitY: Float, hitZ: Float): Boolean
}