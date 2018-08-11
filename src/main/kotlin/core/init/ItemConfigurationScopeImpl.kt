package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import therealfarfetchd.quacklib.api.events.init.item.EventAttachComponent
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.item.init.ItemLinkScope
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.item.init.ItemLinkScopeImpl
import therealfarfetchd.quacklib.render.client.model.ModelError
import therealfarfetchd.quacklib.render.client.model.ModelPlaceholderItem
import kotlin.reflect.jvm.jvmName

class ItemConfigurationScopeImpl(modid: String, override val name: String, val init: InitializationContextImpl) : ItemConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override val components = mutableListOf<ItemComponent>()

  override var model: Model = ModelPlaceholderItem()

  override fun <T : ItemComponent> apply(component: T): T {
    components += component
    component.onApplied(this)

    MinecraftForge.EVENT_BUS.post(EventAttachComponent(this, component))

    return component
  }

  override fun <T : Model> useModel(model: T): T {
    this.model = model
    return model
  }

  override fun link(op: ItemLinkScope.() -> Unit) {
    ItemLinkScopeImpl(rl).also(op)
  }

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Item $name")

    components.forEach {
      vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
      it.validate(this, vc)
    }

    if (!model.accepts(DataSource.Item::class)) {
      vc.additionalInfo = model::class.simpleName ?: model::class.qualifiedName ?: model::class.jvmName
      vc.error("Renderer doesn't support item rendering!")
      model = ModelError
    }

    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}