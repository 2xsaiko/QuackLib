package therealfarfetchd.quacklib.api.tools

import net.minecraft.launchwrapper.Launch
import org.apache.logging.log4j.LogManager
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI

val isDebugMode: Boolean get() = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

val Logger
  get() = LogManager.getLogger(QuackLibAPI.impl.modContext.currentMod())