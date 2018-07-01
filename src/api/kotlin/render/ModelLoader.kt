package therealfarfetchd.quacklib.api.render

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.init.ValidationContext

interface ModelLoader {

  fun load(rl: ResourceLocation, block: BlockStateContainer, vc: ValidationContext)

}