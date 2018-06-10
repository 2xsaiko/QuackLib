package therealfarfetchd.quacklib.api.block.init

interface BlockInitializationContext {

  fun block(name: String, op: BlockConfigurationScope.() -> Unit)

}