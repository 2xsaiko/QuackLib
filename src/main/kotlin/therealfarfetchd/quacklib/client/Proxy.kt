package therealfarfetchd.quacklib.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import therealfarfetchd.quacklib.client.gui.GuiElementRegistry
import therealfarfetchd.quacklib.client.gui.elements.Dummy
import therealfarfetchd.quacklib.common.Proxy
import therealfarfetchd.quacklib.common.item.Wrench

/**
 * Created by marco on 16.07.17.
 */
class Proxy : Proxy() {

  override fun init(e: FMLInitializationEvent) {
    super.init(e)
    GuiElementRegistry.register("quacklib:dummy", Dummy::class)
  }

  @SubscribeEvent
  fun registerModels(e: ModelRegistryEvent) {
    ModelLoader.setCustomModelResourceLocation(Wrench, 0, ModelResourceLocation(Wrench.registryName, "inventory"))
  }

}