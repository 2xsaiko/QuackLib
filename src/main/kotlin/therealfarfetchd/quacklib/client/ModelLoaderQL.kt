package therealfarfetchd.quacklib.client

import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.resources.IResourceManager
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import therealfarfetchd.quacklib.client.api.model.CachedBakedModel
import java.util.function.Function
import therealfarfetchd.quacklib.client.api.model.IModel as IModelQL

object ModelLoaderQL : ICustomModelLoader {

  val blockModels = mutableMapOf<ResourceLocation, IModelQL>()
  val itemModels = mutableMapOf<ResourceLocation, IModelQL>()

  fun register(block: Block, m: IModelQL) {
    blockModels += block.registryName!! to m
  }

  fun register(item: Item, m: IModelQL) {
    itemModels += item.registryName!! to m
  }

  override fun loadModel(rl: ResourceLocation): IModel {
    val srl = strip(rl)
    val model = if (isItem(rl)) itemModels.getValue(srl) else blockModels.getValue(srl)
    return QLModel(model)
  }

  override fun onResourceManagerReload(resourceManager: IResourceManager) {}

  override fun accepts(rl: ResourceLocation): Boolean {
    val srl = strip(rl)
    return if (isItem(rl)) srl in itemModels
    else srl in blockModels
  }

  fun strip(rl: ResourceLocation): ResourceLocation {
    return ResourceLocation(rl.resourceDomain, rl.resourcePath)
  }

  fun isItem(rl: ResourceLocation): Boolean {
    return (rl as? ModelResourceLocation)?.variant == "inventory"
  }

  class QLModel(val model: therealfarfetchd.quacklib.client.api.model.IModel) : IModel {
    override fun bake(p0: IModelState, p1: VertexFormat, p2: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
      return CachedBakedModel(model, p1)
    }
  }
}