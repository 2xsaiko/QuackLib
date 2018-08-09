package therealfarfetchd.quacklib.core.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.item.init.ItemLinkScope
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.accepts
import therealfarfetchd.quacklib.item.init.ItemLinkScopeImpl
import kotlin.reflect.jvm.jvmName

class ItemConfigurationScopeImpl(modid: String, override val name: String, val init: InitializationContextImpl) : ItemConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override val components = mutableListOf<ItemComponent>()

  override val renderers = mutableListOf<Model>()

  override fun apply(component: ItemComponent) {
    components += component
    component.onApplied(this)
  }

  override fun <T : Model> apply(renderer: T): T {
    renderers += renderer
    return renderer
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

    renderers
      .filter { !it.accepts<DataSource.Item>() }
      .forEach {
        vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
        vc.error("Renderer doesn't support item rendering!")
        renderers -= it
      }
    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}