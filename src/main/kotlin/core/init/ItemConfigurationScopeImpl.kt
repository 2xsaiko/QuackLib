package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import kotlin.reflect.jvm.jvmName

class ItemConfigurationScopeImpl(modid: String, override val name: String, val init: InitializationContextImpl) : ItemConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var components: List<ItemComponent> = emptyList()

  override fun apply(component: ItemComponent) {
    components += component
    component.onApplied(this)
  }

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Item $name")

    components.forEach {
      vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
      it.validate(this, vc)
    }
    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}