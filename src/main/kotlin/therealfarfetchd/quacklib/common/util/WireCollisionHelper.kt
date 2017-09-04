package therealfarfetchd.quacklib.common.util

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import therealfarfetchd.quacklib.common.extensions.plus
import therealfarfetchd.quacklib.common.extensions.rotate
import therealfarfetchd.quacklib.common.extensions.rotateY
import therealfarfetchd.quacklib.common.wires.BlockWire

object WireCollisionHelper {
  fun collides(wire: BlockWire, side: EnumFacing): Boolean {
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