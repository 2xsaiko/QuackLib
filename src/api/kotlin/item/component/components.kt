package therealfarfetchd.quacklib.api.item.component

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.init.Applyable
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope

private typealias Base = ItemComponent

interface ItemComponent : Applyable<ItemConfigurationScope>

interface ItemComponentTool : Base {

  val toolTypes: Set<Tool>

}

interface ItemComponentUse : Base {

  fun onUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, hitSide: EnumFacing, hitVec: Vec3): EnumActionResult

}