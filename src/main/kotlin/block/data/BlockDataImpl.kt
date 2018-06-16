package therealfarfetchd.quacklib.block.data

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.api.block.data.BlockData

data class BlockDataImpl(override val world: World, override val pos: BlockPos, override val state: IBlockState) : BlockData