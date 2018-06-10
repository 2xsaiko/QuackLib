package therealfarfetchd.quacklib.core.init

import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.core.mod.CommonProxy

class InitializationContextImpl(val mod: BaseMod) : InitializationContext {

  override fun block(name: String, op: BlockConfigurationScope.() -> Unit) {
    val conf = BlockConfigurationScopeImpl(mod.modid, name).also(op)
    (mod.proxy as CommonProxy).addBlockTemplate(conf)
  }

  override fun item(name: String, op: ItemConfigurationScope.() -> Unit) {
    val conf = ItemConfigurationScopeImpl(mod.modid, name).also(op)
    (mod.proxy as CommonProxy).addItemTemplate(conf)
  }

  override fun tab(name: String, icon: ItemReference) {
    val conf = TabConfigurationImpl(mod.modid, name, icon)
    (mod.proxy as CommonProxy).addTabTemplate(conf)
  }

}