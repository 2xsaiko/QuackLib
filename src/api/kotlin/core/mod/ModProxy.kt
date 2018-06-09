package therealfarfetchd.quacklib.api.core.mod

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

interface ModProxy {

  var mod: BaseMod

  var customProxy: Any?

  fun preInit(e: FMLPreInitializationEvent)

  fun init(e: FMLInitializationEvent)

  fun postInit(e: FMLPostInitializationEvent)

}