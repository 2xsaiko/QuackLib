package therealfarfetchd.quacklib.render.client

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.client.model.*

object ModelLoaderQuackLib : ICustomModelLoader {

  override fun loadModel(modelLocation: ResourceLocation): IModel = try {
    val mt = getModelTypeForRL(modelLocation)

    when (mt) {
      is ModelLoaderQuackLib.ModelType.Block -> loadModelBlock(modelLocation, mt.block)
      is ModelLoaderQuackLib.ModelType.Item -> loadModelItem(modelLocation, mt.item)
      null -> error("unexpected state")
    }
  } catch (e: Exception) {
    logException(e)
    ModelError
  }

  fun loadModelBlock(modelLocation: ResourceLocation, block: BlockTypeImpl): IModel {
    if (block.conf.renderers.isEmpty())
      return ModelPlaceholderBlock(modelLocation, block)

    return ModelStaticBlock(block.conf.renderers)
  }

  fun loadModelItem(modelLocation: ResourceLocation, item: ItemTypeImpl): IModel {
    if (item.conf.renderers.isEmpty())
      return ModelPlaceholderItem(modelLocation, item)

    val data = DataSource.Item(item, ItemRenderStateImpl(item))

    return ModelStatic(item.conf.renderers, data)
  }

  override fun onResourceManagerReload(resourceManager: IResourceManager) {}

  override fun accepts(modelLocation: ResourceLocation): Boolean {
    return getModelTypeForRL(modelLocation) != null
  }

  private fun ResourceLocation.clean(): ResourceLocation =
    if (this::class != ResourceLocation::class) ResourceLocation(namespace, path)
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