package therealfarfetchd.quacklib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import therealfarfetchd.quacklib.common.Scheduler
import therealfarfetchd.quacklib.common.item.ItemBlockMultipartSideAware
import therealfarfetchd.quacklib.common.qblock.QBContainerMultipart
import therealfarfetchd.quacklib.common.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.test.TestQB

/**
 * Created by marco on 08.07.17.
 */

const val ModID = "quacklib"

@Mod(modid = ModID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object QuackLib {
  val debug = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

  internal val Logger = LogManager.getLogger(ModID)!!

  internal val testblock: QBContainerMultipart = QBContainerMultipart(ResourceLocation(ModID, "testblock1"), ::TestQB)
  internal val tbitem: Item = ItemBlockMultipartSideAware(testblock, testblock).also { it.setRegistryName("$ModID:testblock1") }

  init {
    MinecraftForge.EVENT_BUS.register(this)
  }

  @Suppress("UNUSED_PARAMETER", "unused")
  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    if (debug) Logger.log(Level.INFO, "Running in a dev environment; enabling debug features!")

    // register tile entities that come with the library
    GameRegistry.registerTileEntity(QBContainerTile::class.java, "$ModID:qblock_container")
    GameRegistry.registerTileEntity(QBContainerTile.Ticking::class.java, "$ModID:qblock_container_t")

    if (Loader.isModLoaded("mcmultipart")) {
      GameRegistry.registerTileEntity(QBContainerTileMultipart::class.java, "$ModID:qblock_container_mp")
      GameRegistry.registerTileEntity(QBContainerTileMultipart.Ticking::class.java, "$ModID:qblock_container_mp_t")
    }
  }

  @Suppress("unused")
  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    if (!debug) return
    e.registry.register(testblock)
  }

  @Suppress("unused")
  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    if (!debug) return
    e.registry.register(tbitem)
  }

  @Suppress("unused")
  @SubscribeEvent
  fun worldTick(e: TickEvent.WorldTickEvent) {
    if (e.side.isServer) Scheduler.tick()
  }

}