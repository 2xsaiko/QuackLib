package therealfarfetchd.quacklib.render.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
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
    val mt = getModelTypeForRL(modelLocation)

    return when (mt) {
      is ModelLoaderQuackLib.ModelType.Block -> {
        ModelPlaceholderBlock(modelLocation, mt.block)
      }
      is ModelLoaderQuackLib.ModelType.Item -> {
        ModelPlaceholderItem(modelLocation, mt.item)
      }
      null -> error("unexpected state")
    }
  }

  override fun onResourceManagerReload(resourceManager: IResourceManager) {}

  override fun accepts(modelLocation: ResourceLocation): Boolean {
    return getModelTypeForRL(modelLocation) != null
  }

  private fun ResourceLocation.clean(): ResourceLocation =
    if (this::class != ResourceLocation::class) ResourceLocation(resourceDomain, resourcePath)
    else this

  private fun getModelTypeForRL(rl: ResourceLocation): ModelType? {
    val mrl = rl as? ModelResourceLocation ?: return null
    if (mrl.variant == "inventory") {
      val item = item(rl.clean()) as? ItemTypeImpl ?: return null
      return ModelType.Item(item)
    } else {
      val block = block(rl.clean()) as? BlockTypeImpl ?: return null
      return ModelType.Block(block)
    }
  }

  private sealed class ModelType {
    data class Block(val block: BlockTypeImpl) : ModelType()
    data class Item(val item: ItemTypeImpl) : ModelType()
  }

}