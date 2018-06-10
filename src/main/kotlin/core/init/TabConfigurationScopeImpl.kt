package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope

class TabConfigurationScopeImpl(modid: String, name: String, override val icon: ItemReference) : TabConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var items: List<ItemReference> = emptyList()

  override fun include(item: ItemReference) {
    items += item
  }

}