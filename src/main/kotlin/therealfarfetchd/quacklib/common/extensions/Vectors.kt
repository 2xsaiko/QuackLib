package therealfarfetchd.quacklib.common.extensions

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Created by marco on 08.07.17.
 */

operator fun AxisAlignedBB.plus(vec: Vec3i): AxisAlignedBB = this.offset(BlockPos(vec))

operator fun AxisAlignedBB.plus(vec: Vec3d): AxisAlignedBB = this.offset(vec)

operator fun AxisAlignedBB.minus(vec: Vec3i): AxisAlignedBB = this.offset(-BlockPos(vec))

operator fun AxisAlignedBB.minus(vec: Vec3d): AxisAlignedBB = this.offset(-vec)

operator fun Vec3i.unaryMinus(): BlockPos = BlockPos(-this.x, -this.y, -this.z)

operator fun Vec3d.unaryMinus(): Vec3d = Vec3d(-this.x, -this.y, -this.z)

operator fun Vec3i.plus(other: Vec3i): BlockPos = BlockPos(this.x + other.x, this.y + other.y, this.z + other.z)

operator fun Vec3d.plus(other: Vec3d): Vec3d = Vec3d(this.x + other.x, this.y + other.y, this.z + other.z)

operator fun Vec3i.minus(other: Vec3i): BlockPos = BlockPos(this.x - other.x, this.y - other.y, this.z - other.z)

operator fun Vec3d.minus(other: Vec3d): Vec3d = Vec3d(this.x - other.x, this.y - other.y, this.z - other.z)

fun Vec3i.getFacing(): EnumFacing = EnumFacing.getFacingFromVector(x.toFloat(), y.toFloat(), z.toFloat())

fun Vec3d.getFacing(): EnumFacing = EnumFacing.getFacingFromVector(x.toFloat(), y.toFloat(), z.toFloat())