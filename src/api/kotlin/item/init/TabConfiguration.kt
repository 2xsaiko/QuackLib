package therealfarfetchd.quacklib.api.item.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Describable
import therealfarfetchd.quacklib.api.objects.item.ItemType

interface TabConfiguration : Describable {

  val name: String

  val rl: ResourceLocation

  val icon: ItemType

  val items: List<ItemType>

  override fun describe(): String = "Tab $name"

}