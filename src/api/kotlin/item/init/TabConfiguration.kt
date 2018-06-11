package therealfarfetchd.quacklib.api.item.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Describable
import therealfarfetchd.quacklib.api.item.ItemReference

interface TabConfiguration : Describable {

  val name: String

  val rl: ResourceLocation

  val icon: ItemReference

  val items: List<ItemReference>

  override fun describe(): String = "Tab $name"

}