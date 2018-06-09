package therealfarfetchd.quacklib.core

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.tools.internal.ModContext

const val ModID = "quacklib"

@Mod(modid = ModID, useMetadata = true)
object QuackLib {

  init {
    QuackLibAPI.impl = APIImpl
  }

  @SubscribeEvent
  fun preInit(e: FMLPreInitializationEvent) {
    ModContext.dissociate("therealfarfetchd.quacklib.api", recursive = true)
  }

}

fun main(args: Array<String>) {
  QuackLib
}

object APIImpl : QuackLibAPI