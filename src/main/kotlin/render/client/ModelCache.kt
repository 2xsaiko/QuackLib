package therealfarfetchd.quacklib.render.client

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.block.render.BlockRenderStateImpl
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.bake
import therealfarfetchd.quacklib.render.client.model.BakedModelBuilder
import therealfarfetchd.quacklib.render.client.model.ModelError
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
import therealfarfetchd.quacklib.render.vanilla.Transformation

class ModelCache {

  val textures: Set<ResourceLocation> by lazy {
    val result = mutableSetOf<ResourceLocation>()

    ItemTypeImpl.map.values
      .flatMapTo(result) { it.model.getUsedTextures() }

    BlockTypeImpl.map.values
      .flatMapTo(result) { it.model.getUsedTextures() }

    result += setOf(
      ResourceLocation(ModID, "pablo"),
      ResourceLocation(ModID, "error"))

    result
  }

  private val blockmodels = mutableMapOf<KeyBlock, List<BakedQuad>>()
  private val itemmodels = mutableMapOf<KeyItem, List<BakedQuad>>()

  private val models = mutableMapOf<Any, Model>()

  private val texGetter = { rl: ResourceLocation -> AtlasTextureImpl(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(rl.toString())) }

  private fun getModel(item: ItemType): Model {
    return models.computeIfAbsent(item) { item.model }
  }

  private fun getModel(block: BlockType): Model {
    return models.computeIfAbsent(block) { block.model }
  }

  fun getQuadsBlock(rl: ModelResourceLocation, format: VertexFormat, state: IBlockState, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyBlock(state, side)
    return blockmodels.computeIfAbsent(key) {
      val block = block(state.block)
      val src = DataSource.Block(block, BlockRenderStateImpl(block, state))
      val quads = try {
        getModel(block).getStaticRender(src, texGetter)
      } catch (e: Exception) {
        ModelError.getStaticRender(src, texGetter)
      }
      quads.filter { isQuadAtFace(it, side) }.map { it.bake(format) }
    }
  }

  fun getQuadsItem(rl: ModelResourceLocation, format: VertexFormat, stack: ItemStack, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyItem(rl, side)
    return itemmodels.computeIfAbsent(key) {
      val item = item(stack.item)
      val quads = getModel(item).getStaticRender(DataSource.Item(item, ItemRenderStateImpl(item, stack)), texGetter)
      quads.filter { isQuadAtFace(it, side) }.map { it.bake(format) }
    }
  }

  fun getParticleTexture(rl: ModelResourceLocation): TextureAtlasSprite {
    return (getBlockForResource(rl).model.getParticleTexture(texGetter) as AtlasTextureImpl).tas
  }

  fun isAmbientOcclusion(rl: ModelResourceLocation): Boolean {
    return true
  }

  fun isGui3d(rl: ModelResourceLocation): Boolean {
    return true
  }

  fun getTransformation(rl: ModelResourceLocation): Transformation {
    return if (getModelForResource(rl).isItemTransformation()) BakedModelBuilder.defaultItem else BakedModelBuilder.defaultBlock
  }

  fun needsDynamicRenderer(item: ItemType): Boolean {
    return getModel(item).needsDynamicRender()
  }

  private fun getModelForResource(rl: ModelResourceLocation): Model {
    // TODO make this more stable
    return if (rl.variant == "inventory") getItemForResource(rl).model
    else getBlockForResource(rl).model
  }

  private fun getBlockForResource(rl: ModelResourceLocation): BlockType {
    return block(cleanResource(rl)) // TODO: cache
  }

  private fun getItemForResource(rl: ModelResourceLocation): ItemType {
    return item(cleanResource(rl)) // TODO: cache
  }

  private fun cleanResource(rl: ModelResourceLocation): ResourceLocation =
    ResourceLocation(rl.namespace, rl.path)

  data class KeyBlock(val state: IBlockState, val side: EnumFacing?)
  data class KeyItem(val rl: ModelResourceLocation, val side: EnumFacing?)

}

fun isQuadAtFace(q: Quad, f: EnumFacing?): Boolean {
  return BakedModelBuilder.getCullFace(q) == f
}