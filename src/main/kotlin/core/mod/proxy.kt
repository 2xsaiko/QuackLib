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
import therealfarfetchd.quacklib.block.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.core.init.InitializationContextImpl
import therealfarfetchd.quacklib.core.init.ItemConfigurationScopeImpl
import therealfarfetchd.quacklib.core.init.TabConfigurationScopeImpl
import therealfarfetchd.quacklib.item.impl.ItemQuackLib
import therealfarfetchd.quacklib.item.impl.TabQuackLib

sealed class CommonProxy : ModProxy {

  override lateinit var mod: BaseMod

  override var customProxy: Any? = null

  private var blockTemplates: Set<BlockConfigurationScopeImpl> = emptySet()
  private var itemTemplates: Set<ItemConfigurationScopeImpl> = emptySet()
  private var tabTemplates: Set<TabConfigurationScopeImpl> = emptySet()

  var creativeTabs: List<TabQuackLib> = emptyList()

  var failThings: List<String> = emptyList()

  override fun preInit(e: FMLPreInitializationEvent) {
    mod.initContent(InitializationContextImpl(mod))
    tabTemplates.forEach {
      creativeTabs += TabQuackLib(it)
    }
  }

  override fun init(e: FMLInitializationEvent) {

  }

  override fun postInit(e: FMLPostInitializationEvent) {
    blockTemplates.filterNot { it.validate() }.forEach { fatalInit(it.describe()) }
    itemTemplates.filterNot { it.validate() }.forEach { fatalInit(it.describe()) }
    tabTemplates.filterNot { it.validate() }.forEach { fatalInit(it.describe()) }

    if (failThings.isNotEmpty()) {
      error("Failed to add ${failThings.size} objects for mod '${mod.modid}'. Look in the message log for more information.")
    }
  }

  @SubscribeEvent
  fun registerBlocks(e: RegistryEvent.Register<Block>) {
    blockTemplates.forEach {
      Logger.info("Adding ${it.describe()}")
      val block = BlockQuackLib(it)
      e.registry.register(block)
    }
  }

  @SubscribeEvent
  fun registerItems(e: RegistryEvent.Register<Item>) {
    itemTemplates.forEach {
      Logger.info("Adding ${it.describe()}")
      val item = ItemQuackLib(it)
      e.registry.register(item)
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

  private fun fatalInit(text: String) {
    failThings += text
  }

}

class ClientProxy : CommonProxy()

class ServerProxy : CommonProxy()