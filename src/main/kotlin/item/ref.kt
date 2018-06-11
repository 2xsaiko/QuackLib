package therealfarfetchd.quacklib.item

import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.item.ItemReference

data class ItemReferenceDirect(override val mcItem: Item) : ItemReference {

  override val rl: ResourceLocation
    get() = mcItem.registryName!!

  override val exists: Boolean = true

}

data class ItemReferenceByRL(override val rl: ResourceLocation) : ItemReference {

  var item: Item? = null

  override val mcItem: Item
    get() =
      item ?: ForgeRegistries.ITEMS.getValue(rl)
        ?.takeIf { ForgeRegistries.ITEMS.containsKey(rl) }
        ?.also { item = it }
      ?: Items.AIR

  override val exists: Boolean
    get() {
      mcItem
      return item != null
    }

}