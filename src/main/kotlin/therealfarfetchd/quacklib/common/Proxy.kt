package therealfarfetchd.quacklib.common

import mcmultipart.api.multipart.IMultipart
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ITickable
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.QCreativeTab
import therealfarfetchd.quacklib.common.api.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.api.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.api.autoconf.ItemFeature
import therealfarfetchd.quacklib.common.api.block.capability.IConnectable
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.extensions.register
import therealfarfetchd.quacklib.common.api.item.ItemBlockMultipartDelegated
import therealfarfetchd.quacklib.common.api.qblock.*
import therealfarfetchd.quacklib.common.api.util.AutoLoad
import therealfarfetchd.quacklib.common.api.util.IBlockDefinition
import therealfarfetchd.quacklib.common.api.util.IItemDefinition
import therealfarfetchd.quacklib.common.api.util.Scheduler
import therealfarfetchd.quacklib.common.api.wires.TileConnectable
import therealfarfetchd.quacklib.common.block.BlockAlloyFurnace
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.block.MultiblockExtension
import therealfarfetchd.quacklib.common.item.ItemComponent

/**
 * Created by marco on 16.07.17.
 */
open class Proxy {
  private lateinit var asmData: ASMDataTable

  open fun preInit(e: FMLPreInitializationEvent) {
    MinecraftForge.EVENT_BUS.register(this)
    if (Loader.isModLoaded("mcmultipart")) FeatureManager.registerFeature(DefaultFeatures.MultipartMod)
    if (QuackLib.debug) QuackLib.Logger.log(Level.INFO, "Running in a dev environment; enabling debug features!")

    val classes = e.asmData.getAll(AutoLoad::class.java.name)
    val bar = ProgressManager.push("Loading classes", classes.size)
    classes.forEach {
      bar.step(it.javaClass)
      try {
        Class.forName(it.className)
      } catch (e: ClassNotFoundException) {
      } catch (e: LinkageError) {
        e.printStackTrace()
      }
    }
    ProgressManager.pop(bar)

    asmData = e.asmData

    WrapperImplManager.registerModifier(ITickable::class)
    WrapperImplManager.registerModifier(IQBlockMultipart::class)
    WrapperImplManager.registerModifier(IQBlockInventory::class)
    WrapperImplManager.registerModifier(IQBlockMultiblock::class)
    WrapperImplManager.registerWrapper(ITickable::class) {
      te(QBContainerTile::Ticking)
    }
    WrapperImplManager.registerWrapper(IQBlockMultipart::class) {
      container(::QBContainerMultipart)
      te(::QBContainerTileMultipart)
      item { _, block -> ItemBlockMultipartDelegated(block, block as IMultipart) }
    }
    WrapperImplManager.registerWrapper(IQBlockMultipart::class, ITickable::class) {
      inherit(IQBlockMultipart::class)
      te(QBContainerTileMultipart::Ticking)
    }
    WrapperImplManager.registerWrapper(IQBlockInventory::class) {
      container(::QBContainerInventory)
      te(::QBContainerTileInventory)
    }
    WrapperImplManager.registerWrapper(IQBlockInventory::class, ITickable::class) {
      inherit(IQBlockInventory::class)
      te(QBContainerTileInventory::Ticking)
    }
    WrapperImplManager.registerWrapper(IQBlockMultiblock::class) {
      container(::QBContainerMultiblock)
      te(::QBContainerTileMultiblock)
    }
    WrapperImplManager.registerWrapper(IQBlockMultiblock::class, ITickable::class) {
      inherit(IQBlockMultiblock::class)
      te(QBContainerTileMultiblock::Ticking)
    }

    // register tile entities that come with the library
    GameRegistry.registerTileEntity(QBContainerTile::class.java, "$ModID:qblock_container")
    GameRegistry.registerTileEntity(QBContainerTile.Ticking::class.java, "$ModID:qblock_container_t")
    GameRegistry.registerTileEntity(QBContainerTileInventory::class.java, "$ModID:qblock_container_inv")
    GameRegistry.registerTileEntity(QBContainerTileInventory.Ticking::class.java, "$ModID:qblock_container_inv_t")
    GameRegistry.registerTileEntity(QBContainerTileMultiblock::class.java, "$ModID:qblock_container_mb")
    GameRegistry.registerTileEntity(QBContainerTileMultiblock.Ticking::class.java, "$ModID:qblock_container_mb_t")
    GameRegistry.registerTileEntity(MultiblockExtension.Tile::class.java, "$ModID:multiblock")

    CapabilityManager.INSTANCE.register(IConnectable::class)
    CapabilityManager.INSTANCE.register(TileConnectable::class)
    NetworkRegistry.INSTANCE.registerGuiHandler(QuackLib, QGuiHandler)
  }

  open fun init(e: FMLInitializationEvent) {}

  open fun postInit(e: FMLPostInitializationEvent) {
    val bar = ProgressManager.push("Finalizing feature list", 2)
    bar.step("Processing events")
    FeatureManager.enabledFeatures.forEach { it.onGameInit() }
    FeatureManager.printFeatureList()
    bar.step("Checking for valid state")
    FeatureManager.checkFeatures()
    FeatureManager.lockFeatures()
    ProgressManager.pop(bar)
    WrapperImplManager.applyItemMods()
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
    if (FeatureManager.isRequired(DefaultFeatures.AlloyFurnace)) {
      e.registry.register(BlockAlloyFurnace.Item)
    }
    val d = IBlockDefinition.definitions.mapNotNull { it.item } + IItemDefinition.definitions.map { it.item }
    val bar = ProgressManager.push("Registering items", d.size)
    for (item in d) {
      bar.step(item.registryName.toString())
      e.registry.register(item)
    }
    ProgressManager.pop(bar)
  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    IBlockDefinition.populateBlockDefs(asmData)
    IItemDefinition.populateItemDefs(asmData)

    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      e.registry.register(BlockNikoliteOre)
    }
    if (FeatureManager.isRequired(DefaultFeatures.AlloyFurnace)) {
      BlockAlloyFurnace.Block.setCreativeTab(QCreativeTab)
      e.registry.register(BlockAlloyFurnace.Block)
    }
    e.registry.registerAll(*IBlockDefinition.definitions.map { it.block }.toTypedArray())
    e.registry.register(MultiblockExtension.Block)
  }

  @SubscribeEvent
  fun clientTick(e: TickEvent.ClientTickEvent) {
    QBContainer.savedWorld = null
    QBContainer.savedPos = null
    QBContainer.savedNbt = null
    QBContainer.brokenQBlock = emptyMap()
  }

  @SubscribeEvent
  fun serverTick(e: TickEvent.ServerTickEvent) {
    QBContainer.savedWorld = null
    QBContainer.savedPos = null
    QBContainer.savedNbt = null
    QBContainer.brokenQBlock = emptyMap()
  }

  @SubscribeEvent
  fun worldTick(e: TickEvent.WorldTickEvent) {
    if (e.side.isServer) Scheduler.tick()
  }
}