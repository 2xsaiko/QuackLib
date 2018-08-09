package therealfarfetchd.quacklib.render.client.model.new

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.render.client.ModelCache

class BakedModelItemCached(cache: ModelCache, rl: ModelResourceLocation, format: VertexFormat, var stack: ItemStack = ItemStack.EMPTY) : BakedModelCached(cache, rl, format) {

  override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
    return cache.getQuadsItem(rl, format, stack, side, rand)
  }

  override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE

}