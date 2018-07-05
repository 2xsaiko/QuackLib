package therealfarfetchd.quacklib.api.item.component

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.init.Applyable
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

private typealias Base = ItemComponent

interface ItemComponent : Applyable<ItemConfigurationScope>

interface ItemComponentTool : Base {

  val toolTypes: Set<Tool>

}

interface ItemComponentUse : Base {

  fun onUse(stack: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid, hand: EnumHand, hitSide: Facing, hitVec: Vec3): EnumActionResult

}