package therealfarfetchd.quacklib.common.api.block

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IBlockAdvancedOutline {
  /**
   * Replaces getSelectedBoundingBox.
   */
  fun getOutlineBoxes(world: World, pos: BlockPos, state: IBlockState): Collection<AxisAlignedBB>
}