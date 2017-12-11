package therealfarfetchd.quacklib.common.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.IBlockWrenchable
import therealfarfetchd.quacklib.common.api.util.ItemDef

/**
 * Created by marco on 11.07.17.
 */
@ItemDef
object ItemWrench : Item() {
  init {
    registryName = ResourceLocation(ModID, "wrench")
    maxStackSize = 1
  }

  override fun isFull3D(): Boolean = true

  override fun onItemUse(player: EntityPlayer?, world: World, pos: BlockPos, hand: EnumHand?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
    val block = world.getBlockState(pos).block

    val result =
      if (block is IBlockWrenchable) block.rotateBlock(world, pos, facing, player, hitX, hitY, hitZ)
      else block.rotateBlock(world, pos, facing)

    if (result) {
      playWrenchSound(world, pos)
      return EnumActionResult.SUCCESS
    }
    return EnumActionResult.PASS
  }

  fun playWrenchSound(world: World, pos: BlockPos) {
    world.playSound(null, pos, SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.BLOCKS, 1.0f, 1.5f)
  }
}