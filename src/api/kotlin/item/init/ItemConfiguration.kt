package therealfarfetchd.quacklib.api.item.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Describable
import therealfarfetchd.quacklib.api.item.component.ItemComponent

interface ItemConfiguration : Describable {

  /**
   *
   */
  val name: String

  /**
   *
   */
  val rl: ResourceLocation

  /**
   *
   */
  val components: List<ItemComponent>

  override fun describe(): String = "Item $name"

}