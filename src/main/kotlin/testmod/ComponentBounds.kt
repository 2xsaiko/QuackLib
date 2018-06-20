package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.BlockComponentCollision
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentMouseOver
import therealfarfetchd.quacklib.api.block.component.ImportedData
import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.api.core.extensions.AxisAlignedBB
import therealfarfetchd.quacklib.testmod.ComponentBounds.Imported

class ComponentBounds(height: Float) : BlockComponentCollision,
                                       BlockComponentMouseOver,
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

  override fun getCollisionBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB> {
    return listOf(bounds.getValue(data[imported.facing]))
  }

  override fun getRaytraceBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB> =
    getCollisionBoundingBoxes(data)

  class Imported(target: ComponentBounds) : ImportedData<Imported, ComponentBounds>(target) {

    val facing = import<EnumFacing>()

  }

}