package therealfarfetchd.quacklib.api.block.init

interface BlockInitializationContext {

  fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit = {})

}