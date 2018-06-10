package therealfarfetchd.quacklib.item

import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.item.ItemReference

data class ItemReferenceDirect(override val mcItem: Item) : ItemReference {

  override val rl: ResourceLocation
    get() = mcItem.registryName!!

}

data class ItemReferenceByRL(override val rl: ResourceLocation) : ItemReference {

  var item: Item? = null

  override val mcItem: Item
    get() = item?.let {
      it
    } ?: run {
      val i = ForgeRegistries.ITEMS.getValue(rl)
      if (i != null) {
        item = i
        i
      } else {
        Items.AIR
      }
    }

}