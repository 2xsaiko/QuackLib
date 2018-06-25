package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.item.ItemBlockMultipart
import mcmultipart.api.item.ItemBlockMultipart.*
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.MultipartHelper
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.block.data.BlockDataDirectRef
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.block.impl.DataContainer
import therealfarfetchd.quacklib.item.component.prefab.ComponentPlaceBlock

class ComponentPlaceMultipart(val def: BlockConfiguration) : ItemComponentUse {

  val block = block(def.rl)

  @Suppress("NAME_SHADOWING")
  override fun onUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, hitSide: EnumFacing, hitVec: Vec3): EnumActionResult {
    val block = block.mcBlock
    val part = MultipartRegistry.INSTANCE.getPart(block)



    return ItemBlockMultipart.place(player, world, pos, hand, hitSide, hitVec.x, hitVec.y, hitVec.z, stack.item,
      IBlockPlacementInfo(block::getStateForPlacement),
      part,
      IBlockPlacementLogic { stack, player, world, pos, facing, x, y, z, state -> placeBlockAtTested(stack, player, hand, world, pos, facing, x, y, z, state) },
      IPartPlacementLogic(::placePartAtSpecial)
    )
  }

  fun placeBlockAtTested(stack: ItemStack, player: EntityPlayer, hand: EnumHand, world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: IBlockState): Boolean {
    val c = ComponentPlaceBlock.prepareDataContainer(world, pos, player, hand, facing, Vec3(hitX, hitY, hitZ), def)

    if (!checkCollision(c, world, pos)) return false

    return player.canPlayerEdit(pos, facing, stack) &&
           world.getBlockState(pos).block.isReplaceable(world, pos) &&
           block.mcBlock.canPlaceBlockAt(world, pos) &&
           block.mcBlock.canPlaceBlockOnSide(world, pos, facing) &&
           ComponentPlaceBlock.placeBlockAt(block.mcBlock, stack, player, world, pos, newState, c)
  }

  fun placePartAtSpecial(stack: ItemStack, player: EntityPlayer, hand: EnumHand, world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, multipartBlock: IMultipart, state: IBlockState): Boolean {
    multipartBlock as MultipartQuackLib

    val hitVec = Vec3(hitX, hitY, hitZ)
    val c = ComponentPlaceBlock.prepareDataContainer(world, pos, player, hand, facing, hitVec, def)

    if (checkCollision(c, world, pos)) return false

    val slot = multipartBlock.getSlotForPlacement(world, pos, state, facing, hitVec, player, c)

    return if (multipartBlock.canPlacePartAt(world, pos) && multipartBlock.canPlacePartOnSide(world, pos, facing, slot)) {
      MultipartQuackLib.data.set(c)
      if (MultipartHelper.addPart(world, pos, slot, state, false)) {
        if (!world.isRemote) {
          val info = MultipartHelper.getContainer(world, pos).flatMap { c -> c.get(slot) }.orElse(null)
          if (info != null) {
            setMultipartTileNBT(player, stack, info)
            multipartBlock.onPartPlacedBy(info, player, stack)
          }
        }

        true
      } else false
    } else false
  }

  fun checkCollision(c: DataContainer, world: World, pos: BlockPos): Boolean {
    val data = BlockDataDirectRef(c, world, pos)
    val block = block.mcBlock as BlockQuackLib

    val box = block.getCollisionBoundingBox(data) ?: return false

    return world.checkNoEntityCollision(box.offset(pos))
  }

}