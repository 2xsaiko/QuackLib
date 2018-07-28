package therealfarfetchd.quacklib.api.render.model

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.render.BlockRenderState
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

typealias ItemRenderState = Any

interface Model {


}

interface StaticModel : Model {

  fun getStaticRender(getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>

  fun getUsedTextures(): List<ResourceLocation>

}

interface DynamicModel : Model {

  fun getDynamicRender(partialTicks: Float): List<Quad>

}

interface BlockModel : StaticModel {

  fun getStaticRender(bt: BlockType, state: BlockRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    getStaticRender(getTexture)

}

interface BlockModelDyn : DynamicModel {

  fun getDynamicRender(partialTicks: Float, bt: BlockType, state: BlockRenderState): List<Quad> =
    getDynamicRender(partialTicks)

}

interface ItemModel : StaticModel {

  fun getStaticRender(it: ItemType, state: ItemRenderState, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> =
    getStaticRender(getTexture)

}

interface ItemModelDyn : DynamicModel {

  fun getDynamicRender(partialTicks: Float, it: ItemType, state: ItemRenderState): List<Quad> =
    getDynamicRender(partialTicks)

}
