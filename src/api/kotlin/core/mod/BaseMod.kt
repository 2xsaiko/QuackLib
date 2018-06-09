package therealfarfetchd.quacklib.api.core.mod

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import therealfarfetchd.quacklib.api.core.init.InitializationContext
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.tools.Logger

const val KotlinAdapter = "net.shadowfacts.forgelin.KotlinAdapter"

abstract class BaseMod {

  private val clientProxy = "therealfarfetchd.quacklib.core.mod.ClientProxy"
  private val serverProxy = "therealfarfetchd.quacklib.core.mod.ServerProxy"

  var proxy: ModProxy
    private set

  lateinit var modid: String
    private set

  init {
    val proxyClass = if (FMLCommonHandler.instance().side == Side.CLIENT) clientProxy else serverProxy
    proxy = Class.forName(proxyClass).newInstance() as ModProxy
    MinecraftForge.EVENT_BUS.register(proxy)
  }

  fun preInit(e: FMLPreInitializationEvent) {
    modid = e.modMetadata.modId
    Logger.info("Loading '$modid' with QuackLib version ${QuackLibAPI.impl.qlVersion}!")
    proxy.mod = this
    proxy.preInit(e)
  }

  fun init(e: FMLInitializationEvent) {
    proxy.init(e)
  }

  fun postInit(e: FMLPostInitializationEvent) {
    proxy.postInit(e)
  }

  abstract fun initContent(ctx: InitializationContext)

}