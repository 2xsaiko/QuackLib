package therealfarfetchd.quacklib

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import therealfarfetchd.quacklib.common.Scheduler
import therealfarfetchd.quacklib.common.block.QBContainer
import therealfarfetchd.quacklib.common.block.QBContainerTile
import therealfarfetchd.quacklib.common.test.TestQB

/**
 * Created by marco on 08.07.17.
 */

const val ModID = "quacklib"

@Mod(modid = ModID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object QuackLib {
  val debug = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

  internal val Logger = LogManager.getLogger(ModID)!!

  internal val testblock: QBContainer = QBContainer(ResourceLocation("quacklib", "testblock1"), ::TestQB)
  internal val tbitem: Item = ItemBlock(testblock).also { it.setRegistryName("quacklib:testblock1") }

  init {
    MinecraftForge.EVENT_BUS.register(this)
  }

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) {
    if (debug) Logger.log(Level.INFO, "Running in a dev environment; registering test blocks!")
    GameRegistry.registerTileEntity(QBContainerTile::class.java, "qblock_container")
    GameRegistry.registerTileEntity(QBContainerTile.Ticking::class.java, "qblock_container_t")
  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    if (!debug) return
    e.registry.register(testblock)
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    if (!debug) return
    e.registry.register(tbitem)
  }

  @SubscribeEvent
  fun worldTick(e: TickEvent.WorldTickEvent) {
    if (e.side.isServer) Scheduler.tick()
  }

}