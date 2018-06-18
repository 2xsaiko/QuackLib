package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.BlockComponentCollision
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentMouseOver
import therealfarfetchd.quacklib.api.block.component.ImportedData
import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.testmod.ComponentBounds.Imported

class ComponentBounds : BlockComponentCollision,
                        BlockComponentMouseOver,
                        BlockComponentDataImport<ComponentBounds, Imported> {

  override val imported = Imported(this)

  override fun getCollisionBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB> {
    TODO("not implemented")
  }

  override fun getRaytraceBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB> {
    TODO("not implemented")
  }

  class Imported(target: ComponentBounds) : ImportedData<Imported, ComponentBounds>(target) {

    val facing = import<EnumFacing>()

  }

}