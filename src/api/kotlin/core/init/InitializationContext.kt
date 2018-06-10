package therealfarfetchd.quacklib.api.core.init

import therealfarfetchd.quacklib.api.block.init.BlockInitializationContext
import therealfarfetchd.quacklib.api.item.init.ItemInitializationContext

@DslMarker
annotation class InitDSL

@InitDSL
interface InitializationContext : BlockInitializationContext, ItemInitializationContext {

  operator fun invoke(op: InitializationContext.() -> Unit): Unit = with(this, op)

}
