package therealfarfetchd.quacklib.common.api.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.util.IBlockDefinition
import therealfarfetchd.quacklib.common.api.util.IItemDefinition
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.isAccessible

interface IItemSpecialHook {
  private val item
    get() = this as Item

  fun getMeta() = (IItemDefinition.definitions + IBlockDefinition.definitions)
                    .firstOrNull { it.item == item }
                    ?.metaModels ?: setOf(0)

  fun getUnlocalizedNameSuffix(stack: ItemStack) = (".${stack.metadata}".takeIf { getMeta().size > 1 } ?: "")

  fun getSubItemsH(tab: CreativeTabs, items: NonNullList<ItemStack>) {
    if (item.isInCreativeTab(tab)) {
      items.addAll(getMeta().map { item.makeStack(meta = it) })
    }
  }

  private companion object {
    val isInCreativeTab by lazy {
      val method = Item::class.declaredFunctions.find { it.name in setOf("isInCreativeTab", "func_194125_a") }!!
      method.isAccessible = true
      { obj: Item, targetTab: CreativeTabs -> method.call(obj, targetTab) as Boolean }
    }

    fun Item.isInCreativeTab(targetTab: CreativeTabs) = isInCreativeTab(this, targetTab)
  }
}