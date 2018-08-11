package therealfarfetchd.quacklib.testmod

import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.block.component.BlockComponentCollision
import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.block.component.fix
import therealfarfetchd.quacklib.api.block.component.renderProperty
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import kotlin.math.sin

class DummyComp : BlockComponentRenderProperties, BlockComponentCollision {

  override val rl: ResourceLocation = ResourceLocation("qltestmod:dummy")

  val rot = renderProperty<Float>("rotation") {
    output { ((System.currentTimeMillis() % (720 * 50) / 50.0f) * 0.5f) }
  } fix this

  val scale = renderProperty<Float>("scale") {
    output { (sin((System.currentTimeMillis() % (31415 * 50) / 50f) * 0.1f) + 1.75f) / 2.75f }
  } fix this

  override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    return listOf(MCBlockType.FULL_BLOCK_AABB) // FIXME: need other way to make the block
  }

}