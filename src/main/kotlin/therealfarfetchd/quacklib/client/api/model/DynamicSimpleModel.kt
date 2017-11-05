package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.Vec3

abstract class DynamicSimpleModel<in T : QBlock> : SimpleModel(), IDynamicModel<T> {
  override fun bakeDynamicQuads(block: T, playerPos: Vec3): List<Pair<BakedQuad, Boolean>> {
    val builder = ModelBuilder(null)
    builder.setPlayerPos(playerPos)
    addShapesDynamic(block, builder)
    return builder.quads.map { it.bake() to builder.fullbright }
  }

  abstract fun addShapesDynamic(block: T, model: ModelBuilder)
}