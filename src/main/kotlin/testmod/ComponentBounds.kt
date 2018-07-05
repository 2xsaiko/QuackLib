package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.core.extensions.AxisAlignedBB
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.testmod.ComponentBounds.Imported

class ComponentBounds(height: Float) : BlockComponentCollision,
                                       BlockComponentMouseOver,
                                       BlockComponentOcclusion,
                                       BlockComponentDataImport<ComponentBounds, Imported> {

  override val imported = Imported(this)

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
    return listOf(bounds.getValue(block[imported.facing]))
  }

  override fun getOcclusionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    val facing = block[imported.facing]
    val axis = facing.axis

    val xMult = 0.125.takeIf { axis != EnumFacing.Axis.X } ?: 0.0
    val yMult = 0.125.takeIf { axis != EnumFacing.Axis.Y } ?: 0.0
    val zMult = 0.125.takeIf { axis != EnumFacing.Axis.Z } ?: 0.0

    val b = bounds.getValue(facing).contract(xMult, yMult, zMult).contract(-xMult, -yMult, -zMult)
    return listOf(b)
  }

  override fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB> =
    getCollisionBoundingBoxes(block)

  class Imported(target: ComponentBounds) : ImportedData<Imported, ComponentBounds>(target) {

    val facing = import<EnumFacing>()

  }

}