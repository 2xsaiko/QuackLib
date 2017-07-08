package therealfarfetchd.quacklib.common

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.QuackLib

/**
 * Created by marco on 08.07.17.
 */
class TestQB : QBlock() {
  override val material: Material = Material.ROCK

  override fun getDroppedItems(player: EntityPlayer?): List<ItemStack> = listOf(QuackLib.tbitem.makeStack(1))
}