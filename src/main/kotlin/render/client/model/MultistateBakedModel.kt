package therealfarfetchd.quacklib.render.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.util.EnumFacing

abstract class MultistateBakedModel : IBakedModel {

  private val cache = mutableMapOf<IBlockState?, IBakedModel>()

  final override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
    return cache.computeIfAbsent(state, ::createModel).getQuads(state, side, rand)
  }

  abstract fun createModel(state: IBlockState?): IBakedModel

}