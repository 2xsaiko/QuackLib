package therealfarfetchd.quacklib.common

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Created by marco on 08.07.17.
 */

operator fun AxisAlignedBB.plus(vec: Vec3i): AxisAlignedBB = this.offset(BlockPos(vec))
operator fun AxisAlignedBB.plus(vec: Vec3d): AxisAlignedBB = this.offset(vec)