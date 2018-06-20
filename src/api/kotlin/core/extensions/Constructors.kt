package therealfarfetchd.quacklib.api.core.extensions

import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.math.Vec3

fun AxisAlignedBB(x: Float, y: Float, z: Float, x1: Float, y1: Float, z1: Float) =
  AxisAlignedBB(x.toDouble(), y.toDouble(), z.toDouble(), x1.toDouble(), y1.toDouble(), z1.toDouble())

fun AxisAlignedBB(p1: Vec3, p2: Vec3) =
  AxisAlignedBB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)