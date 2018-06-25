package therealfarfetchd.quacklib.block.multipart.cbmp

import codechicken.lib.vec.Cuboid6
import codechicken.lib.vec.Vector3
import codechicken.multipart.JItemMultiPart
import codechicken.multipart.TMultiPart
import net.minecraft.block.Block
import net.minecraft.block.SoundType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentOcclusion
import therealfarfetchd.quacklib.api.block.component.BlockComponentPlacement
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.block.data.BlockDataDirectRef
import therealfarfetchd.quacklib.block.impl.DataContainer

class ComponentPlaceMultipart(val def: BlockConfiguration) : ItemComponentUse {

  override fun onUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, hitSide: EnumFacing, hitVec: Vec3): EnumActionResult {
    return impl.onItemUse(player, world, pos, hand, hitSide, hitVec.x, hitVec.y, hitVec.z)
  }

  val impl = object : JItemMultiPart() {

    override fun newPart(item: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, side: Int, vhit: Vector3): TMultiPart {
      val c = DataContainer()
      c.setConfiguration(def)
      val comps = c.getComponentsOfType<BlockComponentPlacement<BlockDataPart>>()
      val hand = EnumHand.OFF_HAND.takeIf { player.getHeldItem(EnumHand.OFF_HAND) === item }
                 ?: EnumHand.MAIN_HAND

      comps.forEach { it.initialize(world, pos, c.parts.getValue(it.rl), player, hand, EnumFacing.getFront(side), Vec3(vhit.x.toFloat(), vhit.y.toFloat(), vhit.z.toFloat())) }

      val cOcclusion = c.getComponentsOfType<BlockComponentOcclusion>()
      val data = BlockDataDirectRef(c, world, pos)
      MultipartQuackLib.placementBoxes.set((cOcclusion.takeIf { it.isNotEmpty() }?.flatMap { it.getOcclusionBoundingBoxes(data) }
                                            ?: setOf(Block.FULL_BLOCK_AABB)).map(::Cuboid6))

      return MultipartQuackLib(def).also { it.c.import(c) }
    }

    override fun getPlacementSound(item: ItemStack?): SoundType {
      return def.soundType
    }

  }

}