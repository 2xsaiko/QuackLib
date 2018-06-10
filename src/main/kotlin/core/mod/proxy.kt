package therealfarfetchd.quacklib.core.mod

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
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
import therealfarfetchd.quacklib.core.init.InitializationContextImpl
import therealfarfetchd.quacklib.core.init.ItemConfigurationScopeImpl
import therealfarfetchd.quacklib.core.init.TabConfigurationScopeImpl
import therealfarfetchd.quacklib.item.impl.ItemQuackLib
import therealfarfetchd.quacklib.item.impl.TabQuackLib

abstract class CommonProxy : ModProxy {

  override lateinit var mod: BaseMod

  override var customProxy: Any? = null

  private var blockTemplates: Set<BlockConfigurationScopeImpl> = emptySet()
  private var itemTemplates: Set<ItemConfigurationScopeImpl> = emptySet()
  private var tabTemplates: Set<TabConfigurationScopeImpl> = emptySet()

  var creativeTabs: List<TabQuackLib> = emptyList()

  override fun preInit(e: FMLPreInitializationEvent) {
    mod.initContent(InitializationContextImpl(mod))
    tabTemplates.forEach {
      creativeTabs += TabQuackLib(it)
    }
  }

  override fun init(e: FMLInitializationEvent) {

  }

  override fun postInit(e: FMLPostInitializationEvent) {

  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    blockTemplates.forEach {
      if (it.validate()) {
        Logger.info("Adding Block ${it.name}")
        val block = BlockQuackLib(it)
        e.registry.register(block)
      } else fatalInit("Block ${it.name}")
    }
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    itemTemplates.forEach {
      if (it.validate()) {
        Logger.info("Adding Item ${it.name}")
        val item = ItemQuackLib(it)
        e.registry.register(item)
      } else fatalInit("Item ${it.name}")
    }
  }

  @SubscribeEvent
  fun registerEntities(e: RegistryEvent.Register<EntityEntry>) {
    // TODO
  }

  fun addBlockTemplate(bc: BlockConfigurationScopeImpl) {
    blockTemplates += bc
  }

  fun addItemTemplate(ic: ItemConfigurationScopeImpl) {
    itemTemplates += ic
  }

  fun addTabTemplate(tc: TabConfigurationScopeImpl) {
    tabTemplates += tc
  }

  private fun fatalInit(text: String): Nothing =
    error("Failed to initialize: '$text'. Look in the message log for more information.")

}

class ClientProxy : CommonProxy()

class ServerProxy : CommonProxy()