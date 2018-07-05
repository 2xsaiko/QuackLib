package therealfarfetchd.quacklib.api.block.init

import therealfarfetchd.quacklib.api.objects.block.BlockType

interface BlockInitializationContext {

  fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit = {}): BlockType

}