package therealfarfetchd.quacklib.core.mod

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.ModProxy
import therealfarfetchd.quacklib.api.tools.Logger
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.core.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.core.mod.init.InitializationContextImpl

abstract class CommonProxy : ModProxy {

  override lateinit var mod: BaseMod

  override var customProxy: Any? = null

  private var blockTemplates: Set<BlockConfigurationScopeImpl> = emptySet()

  override fun preInit(e: FMLPreInitializationEvent) {
    mod.initContent(InitializationContextImpl(mod))
  }

  override fun init(e: FMLInitializationEvent) {

  }

  override fun postInit(e: FMLPostInitializationEvent) {

  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    blockTemplates.forEach {
      Logger.info("Adding Block ${it.name}")
      val block = BlockQuackLib(it)
      e.registry.register(block)
    }
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {

  }

  @SubscribeEvent
  fun registerEntities(e: RegistryEvent.Register<EntityEntry>) {

  }

  fun addBlockTemplate(bc: BlockConfigurationScopeImpl) {
    blockTemplates += bc
  }

}

class ClientProxy : CommonProxy()

class ServerProxy : CommonProxy()