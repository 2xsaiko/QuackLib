package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.block.MultiblockExtension

@Suppress("OverridingDeprecatedMember")
class QBContainerMultiblock(factory: () -> QBlock) : QBContainer(factory) {
  init {
    checkQBType(IQBlockMultiblock::class)
  }

  // test if other blocks fit too
  override fun canPlaceBlockAt(world: World, pos: BlockPos) =
    super.canPlaceBlockAt(world, pos) &&
    buildPart(world, pos) { (this as IQBlockMultiblock).fillBlocks0(test = true, prePlaceTest = true) }

  override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean =
    super.canPlaceBlockOnSide(world, pos, side) &&
    buildPart(world, pos) { (this as IQBlockMultiblock).fillBlocks0(test = true, prePlaceTest = true) }

  override fun getMobilityFlag(state: IBlockState?) = EnumPushReaction.BLOCK

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    (world.getTileEntity(pos) as QBContainerTileMultiblock).extBlocks
      .map { it + pos }
      .filter { world.getBlockState(it).block == MultiblockExtension.Block }
      .forEach { world.setBlockToAir(it) }
    super.breakBlock(world, pos, state)
  }
}