package therealfarfetchd.quacklib.common.item

import mcmultipart.api.item.ItemBlockMultipart
import mcmultipart.api.multipart.IMultipart
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 09.07.17.
 *
 * MCMultipart's ItemBlockMultipart doesn't check if the block can be placed on a side, so this fixes it.
 */
class ItemBlockMultipartSideAware(block: Block, multipartBlock: IMultipart) : ItemBlockMultipart(block, multipartBlock) {

  override fun placeBlockAtTested(stack: ItemStack?, player: EntityPlayer, world: World, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState?): Boolean {
    return player.canPlayerEdit(pos, facing, stack) && world.getBlockState(pos).block.isReplaceable(world, pos)
           && block.canPlaceBlockAt(world, pos) && block.canPlaceBlockOnSide(world, pos, facing) && super.placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, newState)
  }

}