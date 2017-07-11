package therealfarfetchd.quacklib.common.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.IBlockWrenchable

/**
 * Created by marco on 11.07.17.
 */
object Wrench : Item() {

  init {
    registryName = ResourceLocation(ModID, "wrench")
    unlocalizedName = "$ModID:wrench"
    creativeTab = CreativeTabs.TOOLS
    maxStackSize = 1
  }

  override fun isFull3D(): Boolean = true

  override fun onItemUse(player: EntityPlayer?, world: World, pos: BlockPos, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
    val block = world.getBlockState(pos).block

    val result =
        if (block is IBlockWrenchable) block.rotateBlock(world, pos, facing, player, hitX, hitY, hitZ)
        else block.rotateBlock(world, pos, facing)

    if (result) {
      return EnumActionResult.SUCCESS
    }
    return EnumActionResult.FAIL
  }

}