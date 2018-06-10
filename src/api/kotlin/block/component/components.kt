package therealfarfetchd.quacklib.api.block.component

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.Applyable

data class BlockData(val world: World, val pos: BlockPos, val state: IBlockState) // for now TODO

private typealias Base = BlockComponent
private typealias TE = BlockComponentNeedTE

interface BlockComponent : Applyable<BlockConfigurationScope>

interface BlockComponentNeedTE : Base

interface BlockComponentCapability : TE {

  fun <T> hasCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): Boolean =
    getCapability(data, capability, facing) != null

  fun <T> getCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): T?

}

interface BlockComponentActivation : Base {

  fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean

}

interface BlockComponentTickable : TE {

  fun onTick(data: BlockData)

}
