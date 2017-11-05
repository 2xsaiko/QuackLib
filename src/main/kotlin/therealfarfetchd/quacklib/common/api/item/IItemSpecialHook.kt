package therealfarfetchd.quacklib.common.api.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import kotlin.reflect.full.declaredFunctions

interface IItemSpecialHook {
  private val item
    get() = this as Item

  fun getUnlocalizedNameH(stack: ItemStack): String {
    return "item.${item.unlocalizedName}"
  }

  fun getSubItemsH(tab: CreativeTabs, items: NonNullList<ItemStack>) {
    if (item.isInCreativeTab(tab)) {
      items.add(item.makeStack())
    }
  }

  private companion object {
    val isInCreativeTab by lazy {
      val method = Item::class.declaredFunctions.find { it.name == "isInCreativeTab" }!!
      { obj: Item, targetTab: CreativeTabs -> method.call(obj, targetTab) as Boolean }
    }

    fun Item.isInCreativeTab(targetTab: CreativeTabs) = isInCreativeTab(this, targetTab)
  }
}