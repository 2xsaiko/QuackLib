package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.vec.Vec3

interface IDynamicModel<in T : QBlock> {
  fun bakeDynamicQuads(block: T, playerPos: Vec3): List<Pair<BakedQuad, Boolean>>
}