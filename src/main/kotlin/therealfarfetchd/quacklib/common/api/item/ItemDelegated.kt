package therealfarfetchd.quacklib.common.api.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class ItemDelegated : Item(), IItemSpecialHook {
  override fun getUnlocalizedName(stack: ItemStack) = getUnlocalizedNameH(stack)

  override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) = getSubItemsH(tab, items)
}