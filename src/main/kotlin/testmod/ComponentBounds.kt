package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.core.extensions.AxisAlignedBB
import therealfarfetchd.quacklib.api.objects.block.Block

class ComponentBounds(height: Float) : BlockComponentCollision,
                                       BlockComponentMouseOver,
                                       BlockComponentOcclusion,
                                       BlockComponentDataImport {

  val facing = import<EnumFacing>()

  val bounds = EnumFacing.values().associate {
    it to when (it) {
      EnumFacing.DOWN -> AxisAlignedBB(0f, 0f, 0f, 1f, height, 1f)
      EnumFacing.UP -> AxisAlignedBB(0f, 1f, 0f, 1f, 1 - height, 1f)
      EnumFacing.NORTH -> AxisAlignedBB(0f, 0f, 0f, 1f, 1f, height)
      EnumFacing.SOUTH -> AxisAlignedBB(0f, 0f, 1f, 1f, 1f, 1 - height)
      EnumFacing.WEST -> AxisAlignedBB(0f, 0f, 0f, height, 1f, 1f)
      EnumFacing.EAST -> AxisAlignedBB(1f, 0f, 0f, 1 - height, 1f, 1f)
    }
  }

  override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    return listOf(bounds.getValue(block[facing]))
  }

  override fun getOcclusionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    val facing = block[facing]
    val axis = facing.axis

    val xMult = 0.125.takeIf { axis != EnumFacing.Axis.X } ?: 0.0
    val yMult = 0.125.takeIf { axis != EnumFacing.Axis.Y } ?: 0.0
    val zMult = 0.125.takeIf { axis != EnumFacing.Axis.Z } ?: 0.0

    val b = bounds.getValue(facing).contract(xMult, yMult, zMult).contract(-xMult, -yMult, -zMult)
    return listOf(b)
  }

  override fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB> =
    getCollisionBoundingBoxes(block)

}