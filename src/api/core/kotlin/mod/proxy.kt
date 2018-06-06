package therealfarfetchd.quacklib.api.mod

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import therealfarfetchd.quacklib.api.modinterface.currentMod
import therealfarfetchd.quacklib.tools.internal.ModContext

internal abstract class CommonProxy {

  lateinit var mod: BaseMod

  fun preInit(e: FMLPreInitializationEvent) {
    mod.modid = currentMod()!!.modId
    ModContext.dissociate("therealfarfetchd.quacklib.api", recursive = true)
  }

  fun init(e: FMLInitializationEvent) {}

  fun postInit(e: FMLPostInitializationEvent) {}

}

internal class ClientProxy

internal class ServerProxy