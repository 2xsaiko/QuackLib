package therealfarfetchd.quacklib.api.block.init

import therealfarfetchd.quacklib.api.block.BlockReference

interface BlockInitializationContext {

  fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit = {}): BlockReference

}