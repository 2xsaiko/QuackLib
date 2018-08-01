package therealfarfetchd.quacklib.api.render.model

import net.minecraft.entity.player.EntityPlayer
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.item.render.ItemRenderState
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType

//abstract class UniversalModel : BlockModel, ItemModel {
//
//  final override fun getStaticRender(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
//    getStaticRender(DataSource.Unknown, getTexture)
//
//  final override fun getStaticRender(bt: BlockType, state: BlockRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
//    getStaticRender(DataSource.Block(bt, state), getTexture)
//
//  final override fun getStaticRender(it: ItemType, state: ItemRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
//    getStaticRender(DataSource.Item(it, state), getTexture)
//
//  abstract fun getStaticRender(data: DataSource, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>
//
//}

sealed class DataSource<D : DynDataSource> {
  object Unknown : DataSource<DynDataSource.Unknown>()
  class Block(val bt: BlockType, val state: BlockRenderState) : DataSource<DynDataSource.Block>()
  class Item(val it: ItemType, val state: ItemRenderState) : DataSource<DynDataSource.Item>()
}

sealed class DynDataSource {
  object Unknown : DynDataSource()
  class Block(val player: EntityPlayer) : DynDataSource()
  class Item(val player: EntityPlayer) : DynDataSource()
}