package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess

interface BlockAdvancedOutline {

  fun getSelectedBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos, mouseover: RayTraceResult): AxisAlignedBB?

}