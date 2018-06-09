package therealfarfetchd.quacklib.tools

import org.apache.logging.log4j.LogManager
import therealfarfetchd.quacklib.tools.internal.ModContext

val isDebugMode: Boolean get() = TODO() // Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

val Logger
  get() = LogManager.getLogger(ModContext.currentMod())