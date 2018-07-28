package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

abstract class UniversalModel : BlockModel, ItemModel {

  final override fun getStaticRender(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    getStaticRender(DataSource.Unknown, getTexture)

  final override fun getStaticRender(bt: BlockType, state: BlockRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    getStaticRender(DataSource.Block(bt, state), getTexture)

  final override fun getStaticRender(it: ItemType, state: ItemRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    getStaticRender(DataSource.Item(it, state), getTexture)

  abstract fun getStaticRender(data: DataSource, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>

  sealed class DataSource {
    object Unknown : DataSource()
    class Block(val bt: BlockType, val state: BlockRenderState) : DataSource()
    class Item(val it: ItemType, val state: ItemRenderState) : DataSource() //TODO
  }

}