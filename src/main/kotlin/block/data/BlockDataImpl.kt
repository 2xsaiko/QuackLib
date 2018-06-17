package therealfarfetchd.quacklib.block.data

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.quacklib.api.block.data.BlockData
import therealfarfetchd.quacklib.api.block.data.BlockDataRO

data class BlockDataImpl(override val world: World, override val pos: BlockPos, override val state: IBlockState) : BlockData

data class BlockDataROImpl(override val world: IBlockAccess, override val pos: BlockPos, override val state: IBlockState) : BlockDataRO