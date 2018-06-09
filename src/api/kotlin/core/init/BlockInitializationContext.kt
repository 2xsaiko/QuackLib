package therealfarfetchd.quacklib.api.core.init

import therealfarfetchd.quacklib.api.core.init.block.BlockConfigurationScope

interface BlockInitializationContext {

  fun block(name: String, op: BlockConfigurationScope.() -> Unit = {})

}