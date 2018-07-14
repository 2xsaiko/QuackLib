package therealfarfetchd.quacklib.core.proxy

import com.google.common.collect.ListMultimap
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.event.FMLEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import therealfarfetchd.math.Random
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.tools.access
import therealfarfetchd.quacklib.api.tools.isDebugMode
import therealfarfetchd.quacklib.block.impl.BlockExtraDebug
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.config.QuackLibConfig
import therealfarfetchd.quacklib.core.APIImpl
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.core.QuackLib
import therealfarfetchd.quacklib.core.QuackLib.Logger
import therealfarfetchd.quacklib.core.init.ValidationContextImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.block.CreatedBlockTypeImpl
import therealfarfetchd.quacklib.objects.block.DeferredBlockTypeImpl
import therealfarfetchd.quacklib.objects.item.CreatedItemTypeImpl
import therealfarfetchd.quacklib.objects.item.DeferredItemTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.client.ModelLoaderQuackLib
import therealfarfetchd.quacklib.render.vanilla.VanillaLoader
import therealfarfetchd.quacklib.tools.ModContext
import therealfarfetchd.quacklib.tools.progressbar
import therealfarfetchd.quacklib.tools.registerAnnotatedCapabilities
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Method
import kotlin.math.min
import kotlin.reflect.jvm.javaMethod

sealed class QLCommonProxy {

  open fun preInit(e: FMLPreInitializationEvent) {
    QuackLibConfig
    MinecraftForge.EVENT_BUS.register(this)
    MinecraftForge.EVENT_BUS.register(APIImpl.multipartAPI)
    fixMods()
    APIImpl.qlVersion = e.modMetadata.version
    if (isDebugMode) Logger.warn("Don't forget to add '-Dfml.coreMods.load=therealfarfetchd.quacklib.hax.QuackLibPlugin' to the VM arguments!")
    Logger.info(javaClass.classLoader.getResourceAsStream("assets/quacklib/texts").reader().use { it.readLines() }.let { it[Random.nextInt(it.size)] })
    ModContext.dissociate("therealfarfetchd.quacklib.api", recursive = true)
    ModContext.dissociate("therealfarfetchd.quacklib.tools", recursive = true)

    GameRegistry.registerTileEntity(TileQuackLib::class.java, ResourceLocation(ModID, "tile_quacklib"))

    registerAnnotatedCapabilities(e.asmData)
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  fun resolveItems(e: RegistryEvent.Register<Item>) {
    progressbar("Resolving items", DeferredItemTypeImpl.instances.size + CreatedItemTypeImpl.instances.size) {
      val vc = ValidationContextImpl("Deferred objects")
      DeferredItemTypeImpl.instances.forEach {
        step(it.registryName.toString())
        val type = ItemTypeImpl.getItem(it.registryName)
        if (type == null) vc.error("Item ${it.registryName} does not exist!")
        else it.realInstance = type
      }
      DeferredItemTypeImpl.isInit = false

      CreatedItemTypeImpl.instances.forEach {
        step(it.registryName.toString())
        val type = ItemTypeImpl.getItem(it.registryName)
        if (type == null) vc.error("Item ${it.registryName} does not exist for created item! Something is horribly wrong...")
        else it.realInstance = type
      }
      vc.printMessages()
      if (!vc.isValid()) error("Could not resolve some deferred objects, can't proceed")
    }
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  fun resolveBlocks(e: RegistryEvent.Register<Block>) {
    progressbar("Resolving blocks", DeferredBlockTypeImpl.instances.size + CreatedBlockTypeImpl.instances.size) {
      val vc = ValidationContextImpl("Deferred objects")
      DeferredBlockTypeImpl.instances.forEach {
        step(it.registryName.toString())
        val type = BlockTypeImpl.getBlock(it.registryName)
        if (type == null) vc.error("Block ${it.registryName} does not exist!")
        else it.realInstance = type
      }
      DeferredBlockTypeImpl.isInit = false

      CreatedBlockTypeImpl.instances.forEach {
        step(it.registryName.toString())
        val type = BlockTypeImpl.getBlock(it.registryName)
        if (type == null) vc.error("Block ${it.registryName} does not exist for created block! Something is horribly wrong...")
        else it.realInstance = type
      }
      vc.printMessages()
      if (!vc.isValid()) error("Could not resolve some deferred objects, can't proceed")
    }
  }

  open fun init(e: FMLInitializationEvent) {}

  open fun postInit(e: FMLPostInitializationEvent) {
    run {
      val block = unsafe { block("qltestmod:wallplate").toMCBlockType().blockState }
      val vc = ValidationContextImpl("Model for 'qltestmod:wallplate'")
      VanillaLoader.load(ResourceLocation("qltestmod", "wallplate"), block, vc)
      vc.printMessages()
    }
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

  open fun openResource(rl: ResourceLocation, respectResourcePack: Boolean): InputStream? {
    val cls = ModContext.currentMod()?.mod?.javaClass ?: QuackLib::class.java
    return cls.classLoader.getResourceAsStream("/assets/${rl.resourceDomain}/${rl.resourcePath}")
  }

}

class QLClientProxy : QLCommonProxy() {

  lateinit var mc: Minecraft

  override fun preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)
    mc = Minecraft.getMinecraft()
    ModelLoaderRegistry.registerLoader(ModelLoaderQuackLib)
  }

  @SubscribeEvent
  fun onRenderOverlayText(e: RenderGameOverlayEvent.Text) {
    if (!mc.gameSettings.showDebugInfo) return

    e.left.add(min(e.left.size, 1), "QuackLib ${APIImpl.qlVersion}")

    APIImpl.multipartAPI.onDrawOverlay(mc.world, mc.objectMouseOver, mc.player, e.left, e.right)

    if (mc.objectMouseOver?.typeOfHit == RayTraceResult.Type.BLOCK) {
      val pos = mc.objectMouseOver.blockPos
      val state = mc.world.getBlockState(pos)
      val block = state.block
      if (block is BlockExtraDebug) {
        block.addInformation(mc.world, pos, state.getActualState(mc.world, pos), mc.player, e.left, e.right)
      }
    }
  }

  override fun openResource(rl: ResourceLocation, respectResourcePack: Boolean): InputStream? {
    if (!respectResourcePack) return super.openResource(rl, respectResourcePack)

    return try {
      mc.resourceManager.getResource(rl).inputStream
    } catch (e: IOException) {
      null
    }
  }

}

class QLServerProxy : QLCommonProxy()