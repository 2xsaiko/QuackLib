package therealfarfetchd.quacklib.common.block

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.qblock.*
import therealfarfetchd.quacklib.common.api.util.BlockDef

@BlockDef(registerModels = false)
class MultiblockTest : QBlock(), IQBlockMultiblock {
  override fun FillBlocksScope.fillBlocks() {
    fillOrCancel(BlockPos(-1, 0, -1), BlockPos(1, 3, 1))
  }

  override fun onActivated(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
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