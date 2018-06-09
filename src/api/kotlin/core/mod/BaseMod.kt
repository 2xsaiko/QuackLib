package therealfarfetchd.quacklib.api.core.mod

import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import therealfarfetchd.quacklib.api.core.init.InitializationContext

abstract class BaseMod {

  @Suppress("MemberVisibilityCanBePrivate")
  @SidedProxy(
    clientSide = "therealfarfetchd.quacklib.core.mod.ClientProxy",
    serverSide = "therealfarfetchd.quacklib.core.mod.ServerProxy")
  internal lateinit var proxy: ModProxy

  lateinit var modid: String
    internal set

  @SubscribeEvent
  internal fun `preInit$`(e: FMLPreInitializationEvent) {
    modid = e.modMetadata.modId
    proxy.mod = this
    proxy.preInit(e)
  }

  @SubscribeEvent
  internal fun `init$`(e: FMLInitializationEvent) {
    proxy.init(e)
  }

  @SubscribeEvent
  internal fun `postInit$`(e: FMLPostInitializationEvent) {
    proxy.postInit(e)
  }

  abstract fun init(ctx: InitializationContext)

}