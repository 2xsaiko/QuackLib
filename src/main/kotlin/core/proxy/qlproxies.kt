package therealfarfetchd.quacklib.core.proxy

import com.google.common.collect.ListMultimap
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLModContainer
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.event.FMLEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import therealfarfetchd.math.Random
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.tools.access
import therealfarfetchd.quacklib.api.tools.isDebugMode
import therealfarfetchd.quacklib.block.impl.BlockExtraDebug
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.core.APIImpl
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.core.QuackLib.Logger
import therealfarfetchd.quacklib.tools.ModContext
import therealfarfetchd.quacklib.tools.registerAnnotatedCapabilities
import java.lang.reflect.Method
import kotlin.math.min
import kotlin.reflect.jvm.javaMethod

sealed class CommonProxy {

  open fun preInit(e: FMLPreInitializationEvent) {
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

  open fun init(e: FMLInitializationEvent) {}

  open fun postInit(e: FMLPostInitializationEvent) {}

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

class ClientProxy : CommonProxy() {

  lateinit var mc: Minecraft

  override fun preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)
    mc = Minecraft.getMinecraft()
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

}

class ServerProxy : CommonProxy() {

}