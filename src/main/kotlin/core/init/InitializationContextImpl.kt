package therealfarfetchd.quacklib.core.init

import therealfarfetchd.quacklib.api.block.component.BlockComponentRegistered
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.item.init.TabConfigurationScope
import therealfarfetchd.quacklib.core.mod.CommonProxy
import kotlin.reflect.KClass

class InitializationContextImpl(val mod: BaseMod) : InitializationContext {

  override fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit) {
    val conf = BlockConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addBlockTemplate(conf)
  }

  override fun addItem(name: String, op: ItemConfigurationScope.() -> Unit) {
    val conf = ItemConfigurationScopeImpl(mod.modid, name, this).also(op)
    (mod.proxy as CommonProxy).addItemTemplate(conf)
  }

  override fun addTab(name: String, icon: ItemReference, op: TabConfigurationScope.() -> Unit) {
    val conf = TabConfigurationScopeImpl(mod.modid, name, icon, this).also(op)
    (mod.proxy as CommonProxy).addTabTemplate(conf)
  }

  override fun <T : BlockComponentRegistered> registerComponent(name: String, type: KClass<T>) {

  }

}