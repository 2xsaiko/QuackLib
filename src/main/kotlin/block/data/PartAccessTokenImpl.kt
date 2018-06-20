package therealfarfetchd.quacklib.block.data

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken

class PartAccessTokenImpl<out T : BlockDataPart>(val rl: ResourceLocation) : PartAccessToken<T>