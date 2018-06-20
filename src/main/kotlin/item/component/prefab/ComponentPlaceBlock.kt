package therealfarfetchd.quacklib.item.component.prefab

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.block.component.BlockComponentPlacement
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.block.data.BlockDataDirectRef
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.block.impl.DataContainer
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class ComponentPlaceBlock(val block: BlockReference) : ItemComponentUse {

  @Suppress("NAME_SHADOWING")
  override fun onUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, hitSide: EnumFacing, hitVec: Vec3): EnumActionResult {
    val block = this.block.mcBlock as? BlockQuackLib
                ?: return ItemBlock(block.mcBlock).onItemUse(player, world, pos, hand, hitSide, hitVec.x, hitVec.y, hitVec.z)

    val wstate = world.getBlockState(pos)
    val wblock = wstate.block
    var pos = pos

    if (!wblock.isReplaceable(world, pos)) pos = pos.offset(hitSide)

    val stack = player.getHeldItem(hand)

    val c = DataContainer()
    c.setConfiguration(block.def)
    val comps = c.getComponentsOfType<BlockComponentPlacement<BlockDataPart>>()
    comps.forEach { it.initialize(world, pos, c.parts.getValue(it.rl), player, hand, hitSide, hitVec) }

    if (stack.count > 0 && player.canPlayerEdit(pos, hitSide, stack) && world.mayPlace(block, pos, false, hitSide, null, c)) {

      val meta = 0
      var state = block.getStateForPlacement(world, pos, hitSide, hitVec.x, hitVec.y, hitVec.z, meta, player, hand)

      if (placeBlockAt(stack, player, world, pos, hitSide, hitVec, state, c)) {
        state = world.getBlockState(pos)
        val soundtype = state.block.getSoundType(state, world, pos, player)
        world.playSound(player, pos, soundtype.placeSound, SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F)
        stack.shrink(1)
      }

      return EnumActionResult.SUCCESS
    } else {
      return EnumActionResult.FAIL
    }
  }

  private fun placeBlockAt(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hitSide: EnumFacing, hitVec: Vec3, state: IBlockState, c: DataContainer): Boolean {
    if (!world.setBlockState(pos, state, 11)) return false
    if (state.block == block.mcBlock) {
      ItemBlock.setTileEntityNBT(world, player, pos, stack)
      (world.getTileEntity(pos) as? TileQuackLib)?.c?.import(c)
      block.mcBlock.onBlockPlacedBy(world, pos, state, player, stack)
      if (player is EntityPlayerMP)
        CriteriaTriggers.PLACED_BLOCK.trigger(player, pos, stack)
    }
    return true
  }

  override fun validate(target: ItemConfigurationScope, vc: ValidationContext) {
    super.validate(target, vc)

    if (!block.exists) {
      vc.error("Referenced block ${block.rl} does not exist!")
    }
  }

  private fun World.mayPlace(block: Block, pos: BlockPos, skipCollisionCheck: Boolean, hitSide: EnumFacing, placer: Entity?, c: DataContainer): Boolean {
    if (block !is BlockQuackLib) return mayPlace(block, pos, skipCollisionCheck, hitSide, placer)

    val state = getBlockState(pos)
    val aabb = if (skipCollisionCheck) null else block.getCollisionBoundingBox(state, this, pos, BlockDataDirectRef(c, pos, state, this))
    return if (aabb != null && !checkNoEntityCollision(aabb.offset(pos), placer)) {
      false
    } else {
      state.block.isReplaceable(this, pos) && block.canPlaceBlockOnSide(this, pos, hitSide)
    }
  }

}