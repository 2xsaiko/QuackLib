package therealfarfetchd.quacklib.common.api.util

import mcmultipart.api.slot.EnumFaceSlot
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
  fun collides(world: World, pos: BlockPos, edge: EnumFaceLocation): Boolean {
    if (edge.side == null) return true
    val b1 = world.getQBlock(pos.offset(edge.base.opposite), EnumFaceSlot.fromFace(edge.side)) as? BlockWire<*>
    val b2 = world.getQBlock(pos.offset(edge.side), EnumFaceSlot.fromFace(edge.base.opposite)) as? BlockWire<*>
    return b1?.let { collides(it, edge.base) } ?: false || b2?.let { collides(it, edge.side.opposite) } ?: false
  }

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
}