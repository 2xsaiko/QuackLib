package therealfarfetchd.quacklib.common

import net.minecraft.item.Item
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.block.capability.IConnectable
import therealfarfetchd.quacklib.common.extensions.register
import therealfarfetchd.quacklib.common.item.Wrench
import therealfarfetchd.quacklib.common.qblock.QBContainer
import therealfarfetchd.quacklib.common.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.util.QNBTCompound

/**
 * Created by marco on 16.07.17.
 */
open class Proxy {

  open fun preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this)
    if (QuackLib.debug) QuackLib.Logger.log(Level.INFO, "Running in a dev environment; enabling debug features!")

    // register tile entities that come with the library
    GameRegistry.registerTileEntity(QBContainerTile::class.java, "$ModID:qblock_container")
    GameRegistry.registerTileEntity(QBContainerTile.Ticking::class.java, "$ModID:qblock_container_t")

    if (Loader.isModLoaded("mcmultipart")) {
      GameRegistry.registerTileEntity(QBContainerTileMultipart::class.java, "$ModID:qblock_container_mp")
      GameRegistry.registerTileEntity(QBContainerTileMultipart.Ticking::class.java, "$ModID:qblock_container_mp_t")
    }

    CapabilityManager.INSTANCE.register(IConnectable::class)
  }

  open fun init(e: FMLInitializationEvent) {}

  open fun postInit(e: FMLPostInitializationEvent) {}

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    e.registry.register(Wrench)
  }

  @SubscribeEvent
  fun clientTick(e: TickEvent.ClientTickEvent) {
    QBContainer.savedWorld = null
    QBContainer.savedPos = null
    QBContainer.savedNbt = null
  }

  @SubscribeEvent
  fun serverTick(e: TickEvent.ServerTickEvent) {
    QBContainer.savedWorld = null
    QBContainer.savedPos = null
    QBContainer.savedNbt = null
  }

  @SubscribeEvent
  fun worldTick(e: TickEvent.WorldTickEvent) {
    if (e.side.isServer) Scheduler.tick()
  }

}