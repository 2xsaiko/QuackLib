package therealfarfetchd.quacklib.api.core.init

@DslMarker
annotation class InitDSL

@InitDSL
interface InitializationContext : BlockInitializationContext {

  operator fun invoke(op: InitializationContext.() -> Unit): Unit = with(this, op)

}
