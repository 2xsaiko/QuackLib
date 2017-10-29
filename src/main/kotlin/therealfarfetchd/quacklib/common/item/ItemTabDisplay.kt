package therealfarfetchd.quacklib.common.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.filterFirstNotNull
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.util.IBlockDefinition
import therealfarfetchd.quacklib.common.api.util.IItemDefinition
import therealfarfetchd.quacklib.common.api.util.ItemDef

/**
 * This item is used to display other subitems without them having to override the Item class.
 * Probably temporary :P
 */

@ItemDef(registerModels = false)
object ItemTabDisplay : Item() {
  init {
    registryName = ResourceLocation(ModID, "tab_display")
    maxStackSize = 0
  }

  override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
    (IBlockDefinition.definitions + IItemDefinition.definitions)
      .map { it.item to it.metaModels }
      .filterFirstNotNull()
      .filter { tab in it.first.creativeTabs }
      .forEach { (item, meta) ->
        if (0 !in meta) {
          // try removing the item, but it might not be here
          items.removeIf { it.item == item && it.metadata == 0 }
        }
        meta.filter { it != 0 }.forEach { items.add(item.makeStack(meta = it)) }
      }
  }
}