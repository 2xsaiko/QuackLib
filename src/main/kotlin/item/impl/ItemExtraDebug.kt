package therealfarfetchd.quacklib.item.impl

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHand
import net.minecraft.world.World

interface ItemExtraDebug {

  fun addInformation(world: World, stack: ItemStack, hand: EnumHand, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>)

}