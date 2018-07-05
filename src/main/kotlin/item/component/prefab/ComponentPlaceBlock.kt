package therealfarfetchd.quacklib.item.component.prefab

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.orEmpty
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.toItem
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.api.tools.offset

class ComponentPlaceBlock(val block: BlockType) : ItemComponentUse {

  @Suppress("NAME_SHADOWING")
  override fun onUse(stack: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid, hand: EnumHand, hitSide: Facing, hitVec: Vec3): EnumActionResult {
    val wblock = world.getBlock(pos).orEmpty()
    var pos = pos

    if (!wblock.isReplacable()) pos = pos.offset(hitSide)

    val stack = player.getHeldItem(hand).toItem()

    val newBlock = prepareBlock(block, world, pos, player, hand, hitSide, hitVec)

    return if (stack.count > 0 && player.canPlayerEdit(pos.toMCVec3i(), hitSide, unsafe { stack.mc }) && world.canPlaceBlockAt(newBlock, pos, hitSide, null, true)) {
      if (placeBlockAt(newBlock, stack, player, world, pos)) {
        val b = world.getBlock(pos).orEmpty()
        val soundtype = b.getSoundType(player)
        world.playSound(player, pos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F)
        stack.count--
      }

      EnumActionResult.SUCCESS
    } else {
      EnumActionResult.FAIL
    }
  }

  //  private fun MCWorldMutable.mayPlace(block: BlockType, pos: PositionGrid, skipCollisionCheck: Boolean, hitSide: Facing, placer: Entity?, c: DataContainer): Boolean {
  //    if (block !is BlockQuackLib) return mayPlace(block, pos, skipCollisionCheck, hitSide, placer)
  //
  //    val state = getBlockState(pos)
  //    val aabb = if (skipCollisionCheck) null else block.getCollisionBoundingBox(BlockDataDirectRef(c, this, pos, state))
  //    return if (aabb != null && !checkNoEntityCollision(aabb.offset(pos), placer)) {
  //      false
  //    } else {
  //      state.block.isReplaceable(this, pos) && block.canPlaceBlockOnSide(this, pos, hitSide)
  //    }
  //  }

  companion object {
    fun placeBlockAt(block: Block, item: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid): Boolean {
      if (!world.setBlock(pos, block)) return false
      unsafe {
        val mcPos = pos.toMCVec3i()
        ItemBlock.setTileEntityNBT(world.mc, player, mcPos, item.mc)
        block.onPlaced(player, item)
        if (player is EntityPlayerMP)
          CriteriaTriggers.PLACED_BLOCK.trigger(player, mcPos, item.mc)
      }
      return true
    }

    fun prepareBlock(block: BlockType, world: WorldMutable, pos: PositionGrid, player: EntityPlayer, hand: EnumHand, hitSide: Facing, hitVec: Vec3): Block {
      val nb = block.create()
      unsafe { nb.useRef(world, pos, true) }
      nb.behavior.initialize(nb, player, hand, hitSide, hitVec)
      return nb
    }
  }

}