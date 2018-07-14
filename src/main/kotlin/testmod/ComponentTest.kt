package therealfarfetchd.quacklib.testmod

import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.BlockComponentCollision
import therealfarfetchd.quacklib.api.block.component.BlockComponentMouseOver
import therealfarfetchd.quacklib.api.objects.block.Block

class ComponentTest : BlockComponentCollision, BlockComponentMouseOver {
  val box = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.7, 1.0)

  override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> = listOf(box)

  override fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB> = listOf(box)

}