package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.item.ItemBlockMultipart
import mcmultipart.api.item.ItemBlockMultipart.*
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.MultipartHelper
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlock
import therealfarfetchd.quacklib.api.objects.block.orEmpty
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.MCItem
import therealfarfetchd.quacklib.api.objects.item.toItem
import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.item.component.prefab.ComponentPlaceBlock
import therealfarfetchd.quacklib.objects.world.toWorld

class ComponentPlaceMultipart(val block: BlockType) : ItemComponentUse {

  @Suppress("NAME_SHADOWING")
  override fun onUse(stack: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid, hand: EnumHand, hitSide: Facing, hitVec: Vec3): EnumActionResult {
    val mcb = unsafe { block.mc }
    val mcw = unsafe { world.mc }
    val mci = unsafe { stack.type.mc }
    val part = MultipartRegistry.INSTANCE.getPart(mcb)

    return ItemBlockMultipart.place(player, mcw, pos.toMCVec3i(), hand, hitSide, hitVec.x, hitVec.y, hitVec.z, mci,
      IBlockPlacementInfo { _, _, _, _, _, _, _, _, _ -> mcb.defaultState },
      part,
      IBlockPlacementLogic { stack, player, world, pos, facing, x, y, z, state -> placeBlockAtTested(stack, player, hand, world, pos, facing, x, y, z, state) },
      IPartPlacementLogic(::placePartAtSpecial)
    )
  }

  fun placeBlockAtTested(stack: MCItem, player: EntityPlayer, hand: EnumHand, world: MCWorldMutable, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, newState: MCBlock): Boolean {
    val qw = world.toWorld()
    val qp = pos.toVec3i()
    val b = ComponentPlaceBlock.prepareBlock(block, qw, pos.toVec3i(), player, hand, facing, Vec3(hitX, hitY, hitZ))

    if (checkForCollision(b, world, pos)) return false

    return player.canPlayerEdit(pos, facing, stack) &&
           qw.getBlock(qp).orEmpty().isReplacable() &&
           b.canPlaceBlockAt(qw, qp, facing) &&
           ComponentPlaceBlock.placeBlockAt(b, stack.toItem(), player, qw, qp)
  }

  fun placePartAtSpecial(stack: MCItem, player: EntityPlayer, hand: EnumHand, world: MCWorldMutable, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, multipartBlock: IMultipart, state: MCBlock): Boolean {
    multipartBlock as MultipartQuackLib

    val hitVec = Vec3(hitX, hitY, hitZ)
    val b = ComponentPlaceBlock.prepareBlock(block, world.toWorld(), pos.toVec3i(), player, hand, facing, hitVec)

    if (checkForCollision(b, world, pos)) return false

    val slot = multipartBlock.getSlotForPlacement(facing, hitVec, player, b)

    return if (multipartBlock.canPlacePartAt(world, pos) && multipartBlock.canPlacePartOnSide(world, pos, facing, slot)) {
      MultipartQuackLib.data.set(b)
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

  fun checkForCollision(block: Block, world: MCWorldMutable, pos: BlockPos): Boolean {
    val box = block.getCollisionBoundingBox() ?: return false

    return !world.checkNoEntityCollision(box.offset(pos))
  }

}