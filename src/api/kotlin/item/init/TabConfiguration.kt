package therealfarfetchd.quacklib.api.item.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.ItemReference

interface TabConfiguration {

  val rl: ResourceLocation

  val icon: ItemReference

  val items: List<ItemReference>

}