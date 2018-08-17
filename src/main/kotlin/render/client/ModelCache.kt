package therealfarfetchd.quacklib.render.client

import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.CacheStrategy
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.block.render.CatchingBlockRenderStateImpl
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.bake
import therealfarfetchd.quacklib.render.client.model.BakedModelBuilder
import therealfarfetchd.quacklib.render.client.model.ModelError
import therealfarfetchd.quacklib.render.model.needsTESR
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
import therealfarfetchd.quacklib.render.vanilla.Transformation
import java.util.*

val texGetter = { rl: ResourceLocation -> AtlasTextureImpl(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(rl.toString())) }

class ModelCache {

  val textures: Set<ResourceLocation> by lazy {
    val result = mutableSetOf<ResourceLocation>()

    ItemTypeImpl.map.values
      .flatMapTo(result) { it.model.getUsedTextures() }

    BlockTypeImpl.map.values
      .flatMapTo(result) { it.model.getUsedTextures() }

    result += setOf(
      ResourceLocation(ModID, "pablo"),
      ResourceLocation(ModID, "error"),
      ResourceLocation(ModID, "white"))

    result
  }

  private val blockmodels = mutableMapOf<KeyBlock, List<BakedQuad>>()
  private val itemmodels = mutableMapOf<KeyItem, List<BakedQuad>>()

  fun getQuadsBlock(rl: ModelResourceLocation, format: VertexFormat, state: IBlockState, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyBlock.Full(state, side)
    val block = block(state.block)
    val strat = block.model.getCacheStrategy()
    return when (strat) {
      CacheStrategy.Partial, CacheStrategy.Mixed -> {
        val pkey = key.takeIf { it in blockmodels } ?: blockmodels.keys.asSequence()
          .mapNotNull { it as? KeyBlock.Partial }
          .filter { it.block == state.block }
          .filter { it.side == side }
          .filter { it.pmap.all { (k, v) -> k in state.properties && state.properties[k] == v } }
          .filter { if (state is IExtendedBlockState) it.epmap.all { (k, v) -> k in state.unlistedProperties && state.unlistedProperties[k] == v } else true }
          .firstOrNull()

        return if (pkey != null) {
          val quads = blockmodels.getValue(pkey)
          if (strat == CacheStrategy.Mixed && pkey is KeyBlock.Partial) {
            blockmodels[key] = quads
          }

          quads
        } else {
          val (q, crs) = computeQuads(format, state, side)
          if (crs != null) {
            val pkeyNew = KeyBlock.Partial(
              block = state.block,
              pmap = crs.queriedProperties.associate { it to state.getValueAny(it) },
              epmap = crs.queriedExtProperties.associate { it to Optional.of((state as IExtendedBlockState).getValue(it)) },
              side = side
            )

            blockmodels[pkeyNew] = q
          }

          if (strat == CacheStrategy.Mixed || crs == null) blockmodels[key] = q
          q
        }
      }
      CacheStrategy.Full -> {
        blockmodels.computeIfAbsent(key) { computeQuads(format, state, side).first }
      }
      CacheStrategy.DontCache -> {
        computeQuads(format, state, side).first
      }
    }
  }

  private fun computeQuads(format: VertexFormat, state: IBlockState, side: EnumFacing?): Pair<List<BakedQuad>, CatchingBlockRenderStateImpl?> {
    val block = block(state.block)
    val crs = CatchingBlockRenderStateImpl(block, state)
    val src = DataSource.Block(block, crs)
    var b = false
    val quads = try {
      block.model.getStaticRender(src, texGetter)
    } catch (e: Exception) {
      logException(e)
      b = true
      ModelError.getStaticRender(src, texGetter)
    }
    return Pair(quads.filter { isQuadAtFace(it, side) }.map { it.bake(format) }, crs.takeIf { !b })
  }

  fun getQuadsItem(rl: ModelResourceLocation, format: VertexFormat, stack: ItemStack, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val key = KeyItem(rl, side)
    return itemmodels.computeIfAbsent(key) {
      val item = item(stack.item)
      val src = DataSource.Item(item, ItemRenderStateImpl(item, stack))
      val quads = try {
        item.model.getStaticRender(src, texGetter)
      } catch (e: Exception) {
        logException(e)
        ModelError.getStaticRender(src, texGetter)
      }
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

  fun needsTESR(item: ItemType): Boolean {
    return item.model.needsTESR()
  }

  private fun getModelForResource(rl: ModelResourceLocation): Model {
    // TODO make this more stable
    return if (rl.variant == "inventory") getItemForResource(rl).model
    else getBlockForResource(rl).model
  }

  private fun getBlockForResource(rl: ModelResourceLocation): BlockType {
    return block(cleanResource(rl))
  }

  private fun getItemForResource(rl: ModelResourceLocation): ItemType {
    return item(cleanResource(rl))
  }

  private fun cleanResource(rl: ModelResourceLocation): ResourceLocation =
    ResourceLocation(rl.namespace, rl.path)

  sealed class KeyBlock {

    data class Partial(val block: Block, val pmap: Map<IProperty<*>, Comparable<*>>, val epmap: Map<IUnlistedProperty<*>, Optional<*>>, val side: EnumFacing?) : KeyBlock()

    data class Full(val state: IBlockState, val side: EnumFacing?) : KeyBlock() {

      // FIXME: needed because ExtendedBlockState doesn't implement equals/hashcode correctly. Forge PR?

      override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Full

        if (compareState(state, other.state)) return false
        if (side != other.side) return false

        return true
      }

      override fun hashCode(): Int {
        var result = hashState(state)
        result = 31 * result + (side?.hashCode() ?: 0)
        return result
      }

    }

  }

  data class KeyItem(val rl: ModelResourceLocation, val side: EnumFacing?)

}

fun isQuadAtFace(q: Quad, f: EnumFacing?): Boolean {
  return BakedModelBuilder.getCullFace(q) == f
}

private fun compareState(s1: IBlockState, s2: IBlockState): Boolean {
  return when {
    s1 is IExtendedBlockState && s2 is IExtendedBlockState -> s1.clean == s2.clean && s1.unlistedProperties == s2.unlistedProperties
    s1 is IExtendedBlockState -> s1.clean == s2
    s2 is IExtendedBlockState -> s2.clean == s1
    else -> s1 == s2
  }
}

private fun hashState(state: IBlockState): Int {
  return if (state is IExtendedBlockState) {
    var result = state.clean.hashCode()
    result = 31 * result + state.unlistedProperties.hashCode()
    result
  } else state.hashCode()
}

@Suppress("UNCHECKED_CAST")
private fun IBlockState.getValueAny(p: IProperty<*>): Comparable<Any?> {
  p as IProperty<Comparable<Any?>>
  return getValue(p)
}