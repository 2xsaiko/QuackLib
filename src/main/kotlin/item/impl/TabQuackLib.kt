package therealfarfetchd.quacklib.item.impl

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import therealfarfetchd.quacklib.api.item.init.TabConfiguration

class TabQuackLib(val def: TabConfiguration) : CreativeTabs(def.rl.toString()) {

  override fun getTabIconItem(): ItemStack = def.icon.makeStack()

  override fun displayAllRelevantItems(items: NonNullList<ItemStack>) {
    def.items.forEach {
      val item = it.mcItem
      when {
        item is ItemQuackLib -> item.getSubItems(this, items)
        item.creativeTab != null -> item.getSubItems(item.creativeTab, items)
        else -> {
          item.creativeTab = this
          item.getSubItems(this, items)
          item.creativeTab = null
        }
      }
    }
  }

  override fun toString(): String {
    return "Creative Tab '${def.rl}'"
  }

}