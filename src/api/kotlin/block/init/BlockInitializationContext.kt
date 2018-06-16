package therealfarfetchd.quacklib.api.block.init

import therealfarfetchd.quacklib.api.block.component.BlockComponentRegistered
import kotlin.reflect.KClass

interface BlockInitializationContext {

  fun addBlock(name: String, op: BlockConfigurationScope.() -> Unit = {})

  fun <T : BlockComponentRegistered> registerComponent(name: String, type: KClass<T>)

}

inline fun <reified T : BlockComponentRegistered> BlockInitializationContext.registerComponent(name: String) =
  registerComponent(name, T::class)