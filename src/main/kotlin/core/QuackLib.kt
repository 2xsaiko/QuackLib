package therealfarfetchd.quacklib.core

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.core.proxy.QLCommonProxy
import therealfarfetchd.quacklib.api.tools.Logger as logger

const val ModID = "quacklib"

@Mod(modid = ModID, useMetadata = true, modLanguageAdapter = KotlinAdapter)
object QuackLib {

  val Logger: Logger

  @SidedProxy(
    clientSide = "therealfarfetchd.quacklib.core.proxy.QLClientProxy",
    serverSide = "therealfarfetchd.quacklib.core.proxy.QLServerProxy")
  lateinit var proxy: QLCommonProxy

  init {
    QuackLibAPI.impl = APIImpl
    Logger = logger
  }

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    proxy.preInit(e)
  }

  @Mod.EventHandler
  fun init(e: FMLInitializationEvent) {
    proxy.init(e)
  }

  @Mod.EventHandler
  fun postInit(e: FMLPostInitializationEvent) {
    proxy.postInit(e)
  }

}