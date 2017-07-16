package therealfarfetchd.quacklib

import net.minecraft.item.Item
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import therealfarfetchd.quacklib.common.Proxy
import therealfarfetchd.quacklib.common.item.ItemBlockMultipartSideAware
import therealfarfetchd.quacklib.common.qblock.QBContainerMultipart
import therealfarfetchd.quacklib.common.test.TestQB

/**
 * Created by marco on 08.07.17.
 */

const val ModID = "quacklib"
const val ClientProxy = "therealfarfetchd.$ModID.client.Proxy"
const val ServerProxy = "therealfarfetchd.$ModID.common.Proxy"

@Mod(modid = ModID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object QuackLib {
  val debug = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean

  internal val Logger = LogManager.getLogger(ModID)!!

  internal val testblock: QBContainerMultipart = QBContainerMultipart(ResourceLocation(ModID, "testblock1"), ::TestQB)
  internal val tbitem: Item = ItemBlockMultipartSideAware(testblock, testblock).also { it.setRegistryName("$ModID:testblock1") }

  @SidedProxy(clientSide = ClientProxy, serverSide = ServerProxy)
  lateinit var proxy: Proxy

  @Mod.EventHandler
  fun preInit(e: FMLPreInitializationEvent) = proxy.preInit(e)

  @Mod.EventHandler
  fun init(e: FMLInitializationEvent) = proxy.init(e)

  @Mod.EventHandler
  fun postInit(e: FMLPostInitializationEvent) = proxy.postInit(e)

}