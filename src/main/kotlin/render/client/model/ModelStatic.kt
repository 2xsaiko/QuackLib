//package therealfarfetchd.quacklib.render.client.model
//
//import net.minecraft.client.renderer.block.model.IBakedModel
//import net.minecraft.client.renderer.texture.TextureAtlasSprite
//import net.minecraft.client.renderer.vertex.VertexFormat
//import net.minecraft.util.ResourceLocation
//import net.minecraftforge.client.model.IModel
//import net.minecraftforge.common.model.IModelState
//import therealfarfetchd.quacklib.api.core.extensions.invoke
//import therealfarfetchd.quacklib.api.core.modinterface.block
//import therealfarfetchd.quacklib.api.render.model.DataSource
//import therealfarfetchd.quacklib.api.render.model.Model
//import therealfarfetchd.quacklib.block.render.BlockRenderStateImpl
//import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
//import java.util.function.Function
//
//class ModelStatic(val renderers: List<Model>, val data: DataSource<*>) : IModel {
//  private val textures = renderers.flatMap { it.getUsedTextures() }.toSet()
//
//  override fun getTextures(): Collection<ResourceLocation> = textures
//
//  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
//    return BakedModelBuilder(format) {
//      addQuads(renderers.flatMap { it.getStaticRender(data) { rl -> AtlasTextureImpl(bakedTextureGetter(rl)) } })
//    }
//  }
//}
//
//class ModelStaticBlock(val renderers: List<Model>) : IModel {
//
//  private val textures = renderers.flatMap { it.getUsedTextures() }.toSet()
//
//  override fun getTextures(): Collection<ResourceLocation> = textures + ModelError.ErrorTex
//
//  override fun bake(s: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
//    return MultistateBakedModelBuilder(format) {
//      particleTexture = bakedTextureGetter(getTextures().first()) // FIXME
//      fallbackModel = ModelError.bake(format, bakedTextureGetter)
//      data {
//        if (state != null) {
//          val bt = block(state.block)
//          addQuads(renderers.flatMap { it.getStaticRender(DataSource.Block(bt, BlockRenderStateImpl(bt, state))) { rl -> AtlasTextureImpl(bakedTextureGetter(rl)) } })
//        }
//      }
//    }
//  }
//
//}
//
//class ModelStaticItem(val renderers: List<Model>) : IModel {
//
//  private val textures = renderers.flatMap { it.getUsedTextures() }.toSet()
//
//  override fun getTextures(): Collection<ResourceLocation> = textures + ModelError.ErrorTex
//
//  override fun bake(s: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
//    return MultistateBakedModelBuilder(format) {
//      particleTexture = bakedTextureGetter(getTextures().first()) // FIXME
//      fallbackModel = ModelError.bake(format, bakedTextureGetter)
//      data {
//        if (state != null) {
//          val bt = block(state.block)
//          addQuads(renderers.flatMap { it.getStaticRender(DataSource.Block(bt, BlockRenderStateImpl(bt, state))) { rl -> AtlasTextureImpl(bakedTextureGetter(rl)) } })
//        }
//      }
//    }
//  }
//
//}