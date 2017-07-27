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

operator fun Vec3i.times(i: Int): BlockPos = BlockPos(x * i, y * i, z * i)

operator fun Vec3d.times(d: Double): Vec3d = Vec3d(x * d, y * d, z * d)

fun Vec3i.getFacing(): EnumFacing = EnumFacing.getFacingFromVector(x.toFloat(), y.toFloat(), z.toFloat())

fun Vec3d.getFacing(): EnumFacing = EnumFacing.getFacingFromVector(x.toFloat(), y.toFloat(), z.toFloat())

infix fun Vec3i.distanceTo(other: Vec3i): Double = this.getDistance(other.x, other.y, other.z)

infix fun Vec3d.distanceTo(other: Vec3d): Double = this.distanceTo(other)

operator fun Vec3d.component1(): Double = x
operator fun Vec3d.component2(): Double = y
operator fun Vec3d.component3(): Double = z

operator fun Vec3i.component1(): Int = x
operator fun Vec3i.component2(): Int = y
operator fun Vec3i.component3(): Int = z

fun AxisAlignedBB.rotate(direction: EnumFacing): AxisAlignedBB = when (direction) {
  EnumFacing.UP ->
    AxisAlignedBB(this.minX, 1 - this.minY, this.minZ, this.maxX, 1 - this.maxY, this.maxZ)
  EnumFacing.NORTH ->
    AxisAlignedBB(this.minX, this.minZ, this.minY, this.maxX, this.maxZ, this.maxY)
  EnumFacing.SOUTH ->
    AxisAlignedBB(1 - this.maxX, this.minZ, 1 - this.minY, 1 - this.minX, this.maxZ, 1 - this.maxY)
  EnumFacing.EAST ->
    AxisAlignedBB(1 - this.minY, this.minZ, this.minX, 1 - this.maxY, this.maxZ, this.maxX)
  EnumFacing.WEST ->
    AxisAlignedBB(this.minY, this.minZ, 1 - this.maxX, this.maxY, this.maxZ, 1 - this.minX)
  else -> this
}

fun AxisAlignedBB.rotateY(direction: EnumFacing): AxisAlignedBB {
  var aabb = this
  val flip90: Boolean = (direction.horizontalIndex and 1) != 0
  val flip180: Boolean = (direction.horizontalIndex and 2) != 0
  if (!flip180) aabb = AxisAlignedBB(1 - aabb.minX, aabb.minY, 1 - aabb.minZ, 1 - aabb.maxX, aabb.maxY, 1 - aabb.maxZ)
  if (flip90) aabb = AxisAlignedBB(1 - aabb.minZ, aabb.minY, aabb.minX, 1 - aabb.maxZ, aabb.maxY, aabb.maxX)
  return aabb
}