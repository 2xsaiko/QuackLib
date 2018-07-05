//package therealfarfetchd.quacklib.block.multipart.cbmp
//
//import codechicken.lib.vec.Cuboid6
//import codechicken.lib.vec.Vector3
//import codechicken.multipart.JItemMultiPart
//import codechicken.multipart.TMultiPart
//import net.minecraft.block.SoundType
//import net.minecraft.entity.player.EntityPlayer
//import net.minecraft.item.ItemStack
//import net.minecraft.util.EnumActionResult
//import net.minecraft.util.EnumHand
//import net.minecraft.util.math.BlockPos
//import therealfarfetchd.math.Vec3
//import therealfarfetchd.quacklib.api.block.component.BlockComponentOcclusion
//import therealfarfetchd.quacklib.api.block.component.BlockComponentPlacement
//import therealfarfetchd.quacklib.api.block.data.BlockDataPart
//import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
//import therealfarfetchd.quacklib.api.core.unsafe
//import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
//import therealfarfetchd.quacklib.api.objects.block.Block
//import therealfarfetchd.quacklib.api.objects.block.MCBlockType
//import therealfarfetchd.quacklib.api.objects.getComponentsOfType
//import therealfarfetchd.quacklib.api.objects.item.Item
//import therealfarfetchd.quacklib.api.objects.item.MCItem
//import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
//import therealfarfetchd.quacklib.api.objects.world.WorldMutable
//import therealfarfetchd.quacklib.api.tools.Facing
//import therealfarfetchd.quacklib.api.tools.PositionGrid
//
//class ComponentPlaceMultipart(val block: Block) : ItemComponentUse {
//
//  override fun onUse(stack: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid, hand: EnumHand, hitSide: Facing, hitVec: Vec3): EnumActionResult {
//    return unsafe {
//      impl.onItemUse(player, world.mc, pos.toMCVec3i(), hand, hitSide, hitVec.x, hitVec.y, hitVec.z)
//    }
//  }
//
//  val impl = object : JItemMultiPart() {
//
//    override fun newPart(item: MCItem, player: EntityPlayer, world: MCWorldMutable, pos: BlockPos, side: Int, vhit: Vector3): TMultiPart {
//      val b = block.copy()
//      val comps = b.type.getComponentsOfType<BlockComponentPlacement<BlockDataPart>>()
//      val hand = EnumHand.OFF_HAND.takeIf { player.getHeldItem(EnumHand.OFF_HAND) === item }
//                 ?: EnumHand.MAIN_HAND
//
//      comps.forEach { it.initialize(b, player, hand, Facing.getFront(side), Vec3(vhit.x.toFloat(), vhit.y.toFloat(), vhit.z.toFloat())) }
//
//      val cOcclusion = b.type.getComponentsOfType<BlockComponentOcclusion>()
//      MultipartQuackLib.placementBoxes.set((cOcclusion.takeIf { it.isNotEmpty() }?.flatMap { it.getOcclusionBoundingBoxes(b) }
//                                            ?: setOf(MCBlockType.FULL_BLOCK_AABB)).map(::Cuboid6))
//
//      return MultipartQuackLib(b)
//    }
//
//    override fun getPlacementSound(item: ItemStack?): SoundType {
//      return block.getSoundType(null)
//    }
//
//  }
//
//}