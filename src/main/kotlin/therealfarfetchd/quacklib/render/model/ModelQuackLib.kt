package therealfarfetchd.quacklib.render.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import therealfarfetchd.quacklib.api.core.extensions.invoke
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.render.model.BlockModel
import therealfarfetchd.quacklib.render.client.model.BakedModelBuilder
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
import java.util.function.Function

class ModelBlockQuackLib(val m: BlockModel, val b: BlockType) : IModel {

  override fun getTextures(): Collection<ResourceLocation> =
    m.getUsedTextures()

  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    return BakedModelBuilder(state, format) {
      addQuads(m.getStaticRender(b, TODO()) { rl: ResourceLocation -> AtlasTextureImpl(bakedTextureGetter(rl)) })
    }
  }

}