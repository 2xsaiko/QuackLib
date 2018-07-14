package therealfarfetchd.quacklib.render.client

import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.client.model.ModelPlaceholderBlock
import therealfarfetchd.quacklib.render.client.model.ModelPlaceholderItem

object ModelLoaderQuackLib : ICustomModelLoader {

  override fun loadModel(modelLocation: ResourceLocation): IModel {
    val item = item(modelLocation.clean()) as? ItemTypeImpl
    val block = block(modelLocation.clean()) as? BlockTypeImpl
    return when {
      block != null -> {
        // check block.conf.renderers
        ModelPlaceholderBlock(modelLocation, block)
      }
      item != null -> {
        ModelPlaceholderItem(modelLocation, item)
      }
      else -> error("unexpected state")
    }
  }

  override fun onResourceManagerReload(resourceManager: IResourceManager) {

  }

  override fun accepts(modelLocation: ResourceLocation): Boolean {
    return block(modelLocation.clean()) is BlockTypeImpl || item(modelLocation.clean()) is ItemTypeImpl
  }

  private fun ResourceLocation.clean(): ResourceLocation =
    if (this::class != ResourceLocation::class) ResourceLocation(resourceDomain, resourcePath)
    else this

}