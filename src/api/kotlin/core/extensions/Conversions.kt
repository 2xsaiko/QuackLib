package therealfarfetchd.quacklib.api.core.extensions

import net.minecraft.util.math.Vec3d
import therealfarfetchd.math.Vec3

fun Vec3d.toVec3() = Vec3(x.toFloat(), y.toFloat(), z.toFloat())

fun Vec3.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())