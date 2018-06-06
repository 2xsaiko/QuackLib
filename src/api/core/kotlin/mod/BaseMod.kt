package therealfarfetchd.quacklib.api.mod

import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

abstract class BaseMod {

  @Suppress("MemberVisibilityCanBePrivate")
  @SidedProxy(
    clientSide = "therealfarfetchd.quacklib.api.mod.ClientProxy",
    serverSide = "therealfarfetchd.quacklib.api.mod.ServerProxy")
  internal lateinit var proxy: CommonProxy

  lateinit var modid: String

  @SubscribeEvent
  fun preInit(e: FMLPreInitializationEvent) {
    proxy.mod = this
    proxy.preInit(e)
  }

  @SubscribeEvent
  fun init(e: FMLInitializationEvent) {
    proxy.init(e)
  }

  @SubscribeEvent
  fun postInit(e: FMLPostInitializationEvent) {
    proxy.postInit(e)
  }

}