package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope

class TabConfigurationScopeImpl(modid: String, override val name: String, override val icon: ItemReference, val init: InitializationContextImpl) : TabConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var items: List<ItemReference> = emptyList()

  override fun include(item: ItemReference) {
    items += item
  }

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Tab $name")

    if (!icon.exists) vc.error("Tab icon item ${icon.rl} doesn't exist!")
    items.filterNot(ItemReference::exists).forEach { vc.warn("Item ${it.rl} doesn't exist!") }

    vc.printMessages()
    return vc.isValid()
  }

}