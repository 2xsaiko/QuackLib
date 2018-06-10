package therealfarfetchd.quacklib.item.impl

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import therealfarfetchd.quacklib.api.item.init.TabConfiguration

class TabQuackLib(val def: TabConfiguration) : CreativeTabs(def.rl.toString()) {

  override fun getTabIconItem(): ItemStack = def.icon.makeStack()

  override fun displayAllRelevantItems(items: NonNullList<ItemStack>) {
    def.items.forEach {
      it.mcItem.getSubItems(this, items)
    }
  }

}