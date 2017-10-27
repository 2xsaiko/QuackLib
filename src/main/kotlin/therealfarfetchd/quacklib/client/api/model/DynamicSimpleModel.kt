package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.common.api.qblock.QBlock

abstract class DynamicSimpleModel<in T : QBlock> : SimpleModel(), IDynamicModel<T> {
  override fun bakeDynamicQuads(block: T): List<BakedQuad> {
    val builder = ModelBuilder(null)
    addShapesDynamic(block, builder)
    return builder.quads.map(Quad::bake)
  }

  abstract fun addShapesDynamic(block: T, model: ModelBuilder)
}