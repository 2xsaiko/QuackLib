package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import therealfarfetchd.quacklib.common.api.qblock.QBlock

interface IDynamicModel<in T : QBlock> {
  fun bakeDynamicQuads(block: T): List<BakedQuad>
}