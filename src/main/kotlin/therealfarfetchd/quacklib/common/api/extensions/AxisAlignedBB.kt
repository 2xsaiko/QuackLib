package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.Vec3d
import therealfarfetchd.quacklib.common.api.util.math.OrientedBB

val AxisAlignedBB.min: Vec3d
  get() = Vec3d(minX, minY, minZ)

val AxisAlignedBB.max: Vec3d
  get() = Vec3d(maxX, maxY, maxZ)

fun AxisAlignedBB.asOrientedBB(): OrientedBB = this as? OrientedBB ?: TODO("not implemented")