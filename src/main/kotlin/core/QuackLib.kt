package therealfarfetchd.quacklib.core

import com.google.common.collect.ListMultimap
import core.APIImpl
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Logger
import therealfarfetchd.math.Random
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.KotlinAdapter
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.tools.access
import therealfarfetchd.quacklib.api.tools.isDebugMode
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.tools.ModContext
import java.lang.reflect.Method
import kotlin.reflect.jvm.javaMethod
import therealfarfetchd.quacklib.api.tools.Logger as logger

const val ModID = "quacklib"

@Mod(modid = ModID, useMetadata = true, modLanguageAdapter = KotlinAdapter)
object QuackLib {

  val Logger: Logger

  init {
    QuackLibAPI.impl = APIImpl
    Logger = logger
  }

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    fixMods()
    APIImpl.qlVersion = e.modMetadata.version
    if (isDebugMode) Logger.error("Don't forget to add '-Dfml.coreMods.load=therealfarfetchd.quacklib.hax.QuackLibPlugin' to the VM arguments!")
    Logger.info(javaClass.classLoader.getResourceAsStream("assets/quacklib/texts").reader().use { it.readLines() }.let { it[Random.nextInt(it.size)] })
    ModContext.dissociate("therealfarfetchd.quacklib.api", recursive = true)
    ModContext.dissociate("therealfarfetchd.quacklib.tools", recursive = true)

    GameRegistry.registerTileEntity(TileQuackLib::class.java, ResourceLocation(ModID, "tile_quacklib"))
  }

  @Mod.EventHandler
  fun init(e: FMLInitializationEvent) {
  }

  @Mod.EventHandler
  fun postInit(e: FMLPostInitializationEvent) {
  }

  private fun fixMods() {
    Loader.instance().activeModList
      .filter { it.mod is BaseMod }
      .forEach {
        it as FMLModContainer
        val eventMethods: ListMultimap<Class<out FMLEvent>, Method> = it.access("eventMethods", type = FMLModContainer::class)
        eventMethods.put(FMLPreInitializationEvent::class.java, BaseMod::preInit.javaMethod)
        eventMethods.put(FMLInitializationEvent::class.java, BaseMod::init.javaMethod)
        eventMethods.put(FMLPostInitializationEvent::class.java, BaseMod::postInit.javaMethod)
      }
  }

}