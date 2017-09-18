package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import therealfarfetchd.quacklib.client.api.render.Quad
import therealfarfetchd.quacklib.common.api.qblock.QBlock

abstract class DynamicModelBakery<in T : QBlock> : SimpleModelBakery() {

  fun bakeDynamicQuads(block: T): List<BakedQuad> {
    val quads: MutableList<Quad> = ArrayList()
    addShapesDynamic(block, box(null, quads))
    return quads.map(Quad::bake)
  }

  abstract fun addShapesDynamic(block: T, box: (BoxTemplate.() -> Unit) -> Unit)

}