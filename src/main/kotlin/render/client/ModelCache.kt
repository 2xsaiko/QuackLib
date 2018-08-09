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
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.block.render.BlockRenderStateImpl
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.bake
import therealfarfetchd.quacklib.render.client.model.BakedModelBuilder
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
import therealfarfetchd.quacklib.render.vanilla.Transformation

class ModelCache {

  val textures: Set<ResourceLocation> by lazy {
    val result = mutableSetOf<ResourceLocation>()

    ItemTypeImpl.map.values
      .mapNotNull { it as? ItemTypeImpl }
      .flatMapTo(result) { it.conf.renderers.flatMap(Model::getUsedTextures) }

    BlockTypeImpl.map.values
      .mapNotNull { it as? BlockTypeImpl }
      .flatMapTo(result) { it.conf.renderers.flatMap(Model::getUsedTextures) }

    result
  }

  private val blockmodels = mutableMapOf<KeyBlock, List<BakedQuad>>()
  private val itemmodels = mutableMapOf<KeyItem, List<BakedQuad>>()

  private val texGetter = { rl: ResourceLocation -> AtlasTextureImpl(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(rl.toString())) }

  fun getQuadsBlock(rl: ModelResourceLocation, format: VertexFormat, state: IBlockState, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyBlock(state, side)
    return blockmodels.computeIfAbsent(key) {
      val block = block(state.block)
      val quads = (block as BlockTypeImpl).conf.renderers.flatMap { it.getStaticRender(DataSource.Block(block, BlockRenderStateImpl(block, state)), texGetter) }
      quads.filter { isQuadAtFace(it, side) }.map { it.bake(format) }
    }
  }

  fun getQuadsItem(rl: ModelResourceLocation, format: VertexFormat, stack: ItemStack, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyItem(rl, side)
    return itemmodels.computeIfAbsent(key) {
      val item = item(stack.item)
      val quads = (item as ItemTypeImpl).conf.renderers.flatMap { it.getStaticRender(DataSource.Item(item, ItemRenderStateImpl(item, stack)), texGetter) }
      quads.filter { isQuadAtFace(it, side) }.map { it.bake(format) }
    }
  }

  fun getParticleTexture(rl: ModelResourceLocation): TextureAtlasSprite {
    return Minecraft.getMinecraft().textureMapBlocks.missingSprite
  }

  fun isBuiltInRenderer(rl: ModelResourceLocation): Boolean {
    return false
  }

  fun isAmbientOcclusion(rl: ModelResourceLocation): Boolean {
    return true
  }

  fun isGui3d(rl: ModelResourceLocation): Boolean {
    return true
  }

  fun getTransformation(rl: ModelResourceLocation): Transformation {
    return BakedModelBuilder.defaultBlock
  }

  data class KeyBlock(val state: IBlockState, val side: EnumFacing?)
  data class KeyItem(val rl: ModelResourceLocation, val side: EnumFacing?)

}

fun isQuadAtFace(q: Quad, f: EnumFacing?): Boolean {
  if (f == null) {
    return true // TODO
  } else {
    return false // TODO
  }
}