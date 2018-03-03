package therealfarfetchd.quacklib.common.api.util

import mcmultipart.api.multipart.MultipartOcclusionHelper
import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.api.extensions.rotate
import therealfarfetchd.quacklib.common.api.extensions.rotateY
import therealfarfetchd.quacklib.common.api.wires.BlockWire

object WireCollisionHelper {
  fun collides(wire: BlockWire<*>, side: EnumFacing): Boolean {
    val pos = wire.pos.offset(side)
    val world = wire.actualWorld
    val state = world.getBlockState(pos)
    if (state.block.isAir(state, world, pos)) return false
    val collisionBoxes = ArrayList<AxisAlignedBB>()
    state.addCollisionBoxToList(world, pos, AxisAlignedBB(pos), collisionBoxes, null, false)
    val aabb1 = AxisAlignedBB(BlockPos.ORIGIN.offset(side))
    val wirebb = EnumFacing.HORIZONTALS.map { wire.edgeBounds.rotateY(it).rotate(wire.facing) }.first { it.intersects(aabb1) } + wire.pos
    return collisionBoxes.any { it.intersects(wirebb) }
  }

  //  fun collidesInternal(world: World, pos: BlockPos, self: EnumFacing, other: EnumFacing): Boolean {
  //    val tmc = world.getTileEntity(pos) as? TileMultipartContainer ?: return false
  //    val slot1 = EnumFaceSlot.fromFace(self)
  //    val slot2 = EnumFaceSlot.fromFace(other)
  //    val part1 = tmc.get(slot1).orElse(null) ?: return false
  //    val part2 = tmc.get(slot2).orElse(null) ?: return false
  //    return (MultipartOcclusionHelper.testContainerPartIntersection(tmc, part1, { it in setOf(slot1, slot2) }) &&
  //            MultipartOcclusionHelper.testContainerPartIntersection(tmc, part2, { it in setOf(slot1, slot2) })).also(::println)
  //  }

  fun collidesMultipart(wire: BlockWire<*>, side: EnumFacing): Boolean {
    val pos = wire.pos
    val world = wire.actualWorld
    val tmc = world.getTileEntity(pos) as? TileMultipartContainer ?: return false
    val slot1 = EnumFaceSlot.fromFace(wire.facing)
    val aabb1 = AxisAlignedBB(BlockPos.ORIGIN.offset(side)).grow(0.01)
    val wirebb = EnumFacing.HORIZONTALS.map { wire.extBounds.rotateY(it).rotate(wire.facing) }.first { it.intersects(aabb1) }
    return MultipartOcclusionHelper.testContainerBoxIntersection(tmc, setOf(wirebb), { it == slot1 })
  }
}