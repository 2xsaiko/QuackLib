package therealfarfetchd.quacklib.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.TextureStitchEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.client.gui.GuiElementRegistry
import therealfarfetchd.quacklib.client.gui.GuiLogicRegistry
import therealfarfetchd.quacklib.client.gui.NullGuiLogic
import therealfarfetchd.quacklib.client.gui.elements.Button
import therealfarfetchd.quacklib.client.gui.elements.Dummy
import therealfarfetchd.quacklib.client.gui.elements.Frame
import therealfarfetchd.quacklib.client.gui.elements.Label
import therealfarfetchd.quacklib.client.model.BakedModelRegistry
import therealfarfetchd.quacklib.client.model.CachedBakedModel
import therealfarfetchd.quacklib.client.model.IIconRegister
import therealfarfetchd.quacklib.client.qbr.QBContainerTileRenderer
import therealfarfetchd.quacklib.common.Proxy
import therealfarfetchd.quacklib.common.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.block.BlockNikoliteOre
import therealfarfetchd.quacklib.common.item.ItemComponent
import therealfarfetchd.quacklib.common.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.qblock.QBContainerTileMultipart
import therealfarfetchd.quacklib.common.util.IBlockDefinition
import therealfarfetchd.quacklib.common.util.IItemDefinition

/**
 * Created by marco on 16.07.17.
 */
class Proxy : Proxy() {

  override fun preInit(e: FMLPreInitializationEvent) {
    super.preInit(e)

    ClientRegistry.bindTileEntitySpecialRenderer(QBContainerTile::class.java, QBContainerTileRenderer)
    if (FeatureManager.isRequired(DefaultFeatures.MCMultipartCompat))
      ClientRegistry.bindTileEntitySpecialRenderer(QBContainerTileMultipart::class.java, QBContainerTileRenderer)
  }

  override fun init(e: FMLInitializationEvent) {
    super.init(e)
    with(GuiElementRegistry) {
      register("quacklib:dummy", Dummy::class)
      register("minecraft:frame", Frame::class)
      register("minecraft:label", Label::class)
      register("minecraft:button", Button::class)
    }

    GuiLogicRegistry.register("quacklib:null_logic", NullGuiLogic::class)
  }

  @SubscribeEvent
  fun registerModels(e: ModelRegistryEvent) {
    (IBlockDefinition.definitions + IItemDefinition.definitions)
      .filter { it.item != null && it.registerModels }
      .forEach {
        val mrl = ModelResourceLocation(it.item!!.registryName, "inventory")
        QuackLib.Logger.log(Level.INFO, "Registered model resource location for item ${it.item!!.registryName} to $mrl")
        ModelLoader.setCustomModelResourceLocation(it.item, 0, mrl)
      }

    if (FeatureManager.isRequired(DefaultFeatures.NikoliteOre)) {
      ModelLoader.setCustomModelResourceLocation(BlockNikoliteOre.Item, 0, ModelResourceLocation(BlockNikoliteOre.Item.registryName, "inventory"))
    }

    if (FeatureManager.isRequired(DefaultFeatures.ComponentItem)) {
      for (i in ItemComponent.getValidMetadata()) {
        ModelLoader.setCustomModelResourceLocation(ItemComponent, i, ModelResourceLocation("${ItemComponent.registryName}/$i", "inventory"))
      }
    }
  }

  @SubscribeEvent
  fun bakeModels(e: ModelBakeEvent) {
    CachedBakedModel.clearCache()
    for ((model, mrl) in BakedModelRegistry.models) {
      e.modelRegistry.putObject(mrl, model)
    }
  }

  @SubscribeEvent
  fun textureLoad(e: TextureStitchEvent.Pre) {
    val map = e.map
    if (map.basePath == "textures") {
      for (reg in IIconRegister.iconRegisters) {
        reg.registerIcons(map)
      }
    }
  }
}