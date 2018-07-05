package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope
import therealfarfetchd.quacklib.api.objects.item.ItemType

class TabConfigurationScopeImpl(modid: String, override val name: String, override val icon: ItemType, val init: InitializationContextImpl) : TabConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var items: List<ItemType> = emptyList()

  override fun include(item: ItemType) {
    items += item
  }

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Tab $name")

    vc.printMessages()
    return vc.isValid()
  }

}