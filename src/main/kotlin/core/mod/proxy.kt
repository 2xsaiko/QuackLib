package therealfarfetchd.quacklib.core.mod

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.registry.EntityEntry
import therealfarfetchd.quacklib.api.core.mod.BaseMod
import therealfarfetchd.quacklib.api.core.mod.ModProxy
import therealfarfetchd.quacklib.core.mod.init.InitializationContextImpl

abstract class CommonProxy : ModProxy {

  override lateinit var mod: BaseMod

  override var customProxy: Any? = null

  override fun preInit(e: FMLPreInitializationEvent) {
    mod.initContent(InitializationContextImpl(mod))
  }

  override fun init(e: FMLInitializationEvent) {

  }

  override fun postInit(e: FMLPostInitializationEvent) {

  }

  @Mod.EventHandler
  fun registerBlocks(e: RegistryEvent.Register<Block>) {

  }

  @Mod.EventHandler
  fun registerItems(e: RegistryEvent.Register<Item>) {

  }

  @Mod.EventHandler
  fun registerEntities(e: RegistryEvent.Register<EntityEntry>) {

  }

}

class ClientProxy : CommonProxy()

class ServerProxy : CommonProxy()