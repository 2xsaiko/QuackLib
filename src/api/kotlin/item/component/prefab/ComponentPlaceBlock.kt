package therealfarfetchd.quacklib.api.item.component.prefab

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope

class ComponentPlaceBlock(val block: BlockReference) : ItemComponentUse {

  lateinit var placeItem: ItemBlock

  override fun onUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, hitSide: EnumFacing, hitVec: Vec3): EnumActionResult {
    return placeItem.onItemUse(player, world, pos, hand, hitSide, hitVec.x, hitVec.y, hitVec.z)
  }

  override fun validate(target: ItemConfigurationScope, vc: ValidationContext) {
    super.validate(target, vc)

    if (!block.exists) {
      vc.error("Referenced block ${block.rl} does not exist!")
    } else {
      placeItem = ItemBlock(block.mcBlock)
    }
  }

}