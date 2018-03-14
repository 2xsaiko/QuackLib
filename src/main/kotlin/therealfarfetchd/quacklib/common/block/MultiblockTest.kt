package therealfarfetchd.quacklib.common.block

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.qblock.*
import therealfarfetchd.quacklib.common.api.util.BlockDef
import therealfarfetchd.quacklib.common.api.util.math.getDistance
import kotlin.math.roundToInt

@BlockDef(registerModels = false)
class MultiblockTest : QBlock(), IQBlockMultiblock, ITickable {
  var radius = 0.0

  override fun update() {
    radius += 0.1
    fillBlocks()
  }

  override fun FillBlocksScope.fillBlocks() {
    removeExtBlocks()
    val irad = radius.roundToInt()
    BlockPos.getAllInBox(BlockPos(-irad, 2, -irad), BlockPos(irad, 2, irad))
      .filter { getDistance(it.x.toDouble(), it.z.toDouble()) in radius - 0.5..radius + 0.5 }
      .forEach { placeExtension(it) }
  }

  override fun onActivated(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    radius = 0.0
    fillBlocks()
    return true
  }

  override val material = Material.WOOD

  override val blockType = ResourceLocation(ModID, "multiblock_test")

  override fun getItem() = Item.makeStack()

  companion object {
    val Item by WrapperImplManager.item(MultiblockTest::class)
    val Block by WrapperImplManager.container(MultiblockTest::class)
  }
}