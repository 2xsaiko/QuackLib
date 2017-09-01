package therealfarfetchd.quacklib.client.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ModelResourceLocation

internal object BakedModelRegistry {
  internal var models: Set<Pair<IBakedModel, ModelResourceLocation>> = emptySet()
}

fun IBakedModel.registerBakedModel(mrl: ModelResourceLocation) {
  BakedModelRegistry.models += this to mrl
}