package therealfarfetchd.quacklib.common.item

import mcmultipart.api.item.ItemBlockMultipart
import mcmultipart.api.item.ItemBlockMultipart.place
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.MultipartHelper
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 24.06.17.
 */
open class ItemBlockMultipartEx(block: Block, multipartBlock: IMultipart) : ItemBlockMultipart(block, multipartBlock) {

  override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float,
                         hitY: Float, hitZ: Float): EnumActionResult {
    return place(player, world, pos, hand, facing, hitX, hitY, hitZ, this, this.block::getStateForPlacement, multipartBlock, this::placeBlockAtTested, this::placePartAt_)
  }

  override fun placeBlockAtTested(stack: ItemStack?, player: EntityPlayer, world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState?): Boolean {
    return player.canPlayerEdit(pos, facing, stack) && world.getBlockState(pos).block.isReplaceable(world, pos) &&
           block.canPlaceBlockAt(world, pos) &&
           block.canPlaceBlockOnSide(world, pos, facing) &&
           super.placeBlockAt(stack, player, world, pos, facing, hitX, hitY, hitZ, newState)
  }

  fun placePartAt_(stack: ItemStack, player: EntityPlayer, hand: EnumHand, world: World, pos: BlockPos, facing: EnumFacing,
                   hitX: Float, hitY: Float, hitZ: Float, multipartBlock: IMultipart, state: IBlockState): Boolean {
    if (!block.canPlaceBlockAt(world, pos) || !block.canPlaceBlockOnSide(world, pos, facing)) return false

    val slot = multipartBlock.getSlotForPlacement(world, pos, state, facing, hitX, hitY, hitZ, player)
    if (MultipartHelper.addPart(world, pos, slot, state, false)) {
      if (!world.isRemote) {
        val info = MultipartHelper.getContainer(world, pos).flatMap { c -> c.get(slot) }.orElse(null)
        if (info != null) {
          setMultipartTileNBT(player, stack, info)
          multipartBlock.onPartPlacedBy(info, player, stack)
        }
      }
      return true
    }
    return false
  }

  companion object {
    operator fun <T> invoke(block: T): ItemBlockMultipartEx where T : Block, T : IMultipart {
      return ItemBlockMultipartEx(block, block)
    }
  }

}