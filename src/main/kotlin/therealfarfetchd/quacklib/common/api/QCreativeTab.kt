package therealfarfetchd.quacklib.common.api

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.util.AutoLoad
import therealfarfetchd.quacklib.common.item.ItemWrench

@AutoLoad
object QCreativeTab : CreativeTabs(ModID) {
  override fun getTabIconItem(): ItemStack = ItemWrench.makeStack()
}