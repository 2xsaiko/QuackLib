package therealfarfetchd.quacklib.common

import mcmultipart.api.multipart.IMultipart
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ITickable
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
import net.minecraftforge.oredict.OreDictionary
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.block.capability.IConnectable
import therealfarfetchd.quacklib.common.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.autoconf.ItemFeature
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.extensions.makeStack
import therealfarfetchd.quacklib.common.extensions.register
import therealfarfetchd.quacklib.common.item.ItemBlockMultipartEx
import therealfarfetchd.quacklib.common.item.ItemComponent
import therealfarfetchd.quacklib.common.qblock.*
import therealfarfetchd.quacklib.common.util.AutoLoad
import therealfarfetchd.quacklib.common.util.IBlockDefinition
import therealfarfetchd.quacklib.common.util.IItemDefinition

/**
 * Created by marco on 16.07.17.
 */
open class Proxy {

  open fun preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this)
    if (Loader.isModLoaded("mcmultipart")) FeatureManager.registerFeature(DefaultFeatures.MultipartMod)
    if (Loader.isModLoaded("teckle")) FeatureManager.registerFeature(DefaultFeatures.TeckleMod)
    if (QuackLib.debug) QuackLib.Logger.log(Level.INFO, "Running in a dev environment; enabling debug features!")

    e.asmData.getAll(AutoLoad::class.java.name).forEach { Class.forName(it.className) }

    WrapperImplManager.registerModifier(ITickable::class)
    WrapperImplManager.registerModifier(IQBlockMultipart::class)
    WrapperImplManager.registerWrapper(ITickable::class) {
      te(QBContainerTile::Ticking)
    }
    WrapperImplManager.registerWrapper(IQBlockMultipart::class) {
      te(::QBContainerTileMultipart)
      container { QBContainerMultipart(it) }
      item { _, block -> ItemBlockMultipartEx(block, block as IMultipart) }
    }
    WrapperImplManager.registerWrapper(IQBlockMultipart::class, ITickable::class) {
      inherit(IQBlockMultipart::class)
      te(QBContainerTileMultipart::Ticking)
    }

    IBlockDefinition.populateBlockDefs(e.asmData)
    IItemDefinition.populateItemDefs(e.asmData)

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
    if (FeatureManager.isRequired(DefaultFeatures.ComponentItem)) {
      e.registry.register(ItemComponent)

      FeatureManager.enabledFeatures.mapNotNull { it as? ItemFeature }.forEach { f ->
        f.oreDict.forEach {
          OreDictionary.registerOre(it, ItemComponent.makeStack(meta = f.meta))
        }
      }
    }
    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      e.registry.register(BlockNikoliteOre.Item)
    }
    e.registry.registerAll(*IBlockDefinition.definitions.mapNotNull { it.item }.toTypedArray())
    e.registry.registerAll(*IItemDefinition.definitions.map { it.item }.toTypedArray())
  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      e.registry.register(BlockNikoliteOre)
    }
    e.registry.registerAll(*IBlockDefinition.definitions.map { it.block }.toTypedArray())
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