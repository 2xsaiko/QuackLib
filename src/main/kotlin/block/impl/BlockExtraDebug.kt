package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface BlockExtraDebug {

  fun addInformation(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>)

}