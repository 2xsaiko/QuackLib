package therealfarfetchd.quacklib.common.api

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.extensions.makeStack
import therealfarfetchd.quacklib.common.item.ItemWrench

object QCreativeTab : CreativeTabs(ModID) {
  override fun getTabIconItem(): ItemStack = ItemWrench.makeStack()
}