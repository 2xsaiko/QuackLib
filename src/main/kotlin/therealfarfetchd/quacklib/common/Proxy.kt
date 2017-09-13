package therealfarfetchd.quacklib.common

import net.minecraft.block.Block
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
import therealfarfetchd.quacklib.common.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.extensions.register
import therealfarfetchd.quacklib.common.item.ItemComponent
import therealfarfetchd.quacklib.common.item.ItemWrench
import therealfarfetchd.quacklib.common.qblock.QBContainer
import therealfarfetchd.quacklib.common.qblock.QBContainerTile

/**
 * Created by marco on 16.07.17.
 */
open class Proxy {

  open fun preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this)
    if (QuackLib.debug) QuackLib.Logger.log(Level.INFO, "Running in a dev environment; enabling debug features!")

    if (Loader.isModLoaded("mcmultipart")) FeatureManager.registerFeature(DefaultFeatures.MultipartMod)
    if (Loader.isModLoaded("teckle")) FeatureManager.registerFeature(DefaultFeatures.TeckleMod)

    // register tile entities that come with the library
    GameRegistry.registerTileEntity(QBContainerTile::class.java, "$ModID:qblock_container")
    GameRegistry.registerTileEntity(QBContainerTile.Ticking::class.java, "$ModID:qblock_container_t")

    CapabilityManager.INSTANCE.register(IConnectable::class)
  }

  open fun init(e: FMLInitializationEvent) {}

  open fun postInit(e: FMLPostInitializationEvent) {
    FeatureManager.printFeatureList()
    FeatureManager.checkFeatures()
    FeatureManager.lockFeatures()
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    e.registry.register(ItemWrench)
    if (FeatureManager.isRequired(DefaultFeatures.ComponentItem)) {
      e.registry.register(ItemComponent)
    }
    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      e.registry.register(BlockNikoliteOre.Item)
    }
  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      e.registry.register(BlockNikoliteOre)
    }
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