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
import therealfarfetchd.quacklib.render.client.model.new.ModelCached

object ModelLoaderQuackLib : ICustomModelLoader {

  var cache = ModelCache()

  override fun loadModel(modelLocation: ResourceLocation): IModel =
    ModelCached(cache, modelLocation as? ModelResourceLocation
                       ?: ModelResourceLocation(modelLocation, "unknown"))

  override fun onResourceManagerReload(resourceManager: IResourceManager) {
    cache = ModelCache()
  }

  override fun accepts(modelLocation: ResourceLocation): Boolean {
    val mrl = modelLocation as? ModelResourceLocation ?: return false
    if (mrl.variant == "inventory") {
      item(modelLocation.clean()) as? ItemTypeImpl ?: return false
    } else {
      block(modelLocation.clean()) as? BlockTypeImpl ?: return false
    }
    return true
  }

  private fun ResourceLocation.clean(): ResourceLocation =
    if (this::class != ResourceLocation::class) ResourceLocation(namespace, path)
    else this

}