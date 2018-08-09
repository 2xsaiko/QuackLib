package therealfarfetchd.quacklib.render.client.model.new

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import therealfarfetchd.quacklib.render.client.ModelCache
import java.util.function.Function

class ModelCached(val cache: ModelCache, val rl: ModelResourceLocation) : IModel {

  override fun getTextures(): Collection<ResourceLocation> = cache.textures

  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    return BakedModelCached(cache, rl, format)
  }

}