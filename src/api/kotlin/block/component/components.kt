package therealfarfetchd.quacklib.api.block.component

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.api.core.init.block.BlockConfigurationScope

private typealias Base = BlockComponent
private typealias TE = BlockComponent // BlockComponentNeedTE

interface BlockComponent {

  fun onApplied(blockConfig: BlockConfigurationScope) {}

}

// interface BlockComponentNeedTE : BlockComponent
//
// interface BlockCapabilityProvider : BlockComponentNeedTE {
//
//   fun <T> hasCapability(capability: Capability<T>, facing: EnumFacing?): Boolean =
//     getCapability(capability, facing) != null
//
//   fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T?
//
// }

interface BlockComponentActivation : Base {

  fun onActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean

}

