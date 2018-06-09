package therealfarfetchd.quacklib.core.mod.init

import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.init.block.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.core.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.core.mod.CommonProxy

class InitializationContextImpl(val mod: BaseMod) : InitializationContext {

  override fun block(name: String, op: BlockConfigurationScope.() -> Unit) {
    val conf = BlockConfigurationScopeImpl(mod.modid, name).also(op)
    (mod.proxy as CommonProxy).addBlockTemplate(conf)
  }

}