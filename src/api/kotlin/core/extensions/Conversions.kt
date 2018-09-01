@file:Suppress("NOTHING_TO_INLINE")

package therealfarfetchd.quacklib.api.core.extensions

import net.minecraft.util.math.BlockPos
import therealfarfetchd.math.Mat4
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.Vec3i
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.world.MCWorld
import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import javax.vecmath.Matrix4f
import net.minecraft.util.math.Vec3d as MCVec3d
import net.minecraft.util.math.Vec3i as MCVec3i

fun MCVec3d.toVec3() = Vec3(x.toFloat(), y.toFloat(), z.toFloat())

fun Vec3.toMCVec3d() = MCVec3d(x.toDouble(), y.toDouble(), z.toDouble())

fun MCVec3i.toVec3i() = Vec3i(x, y, z)

fun Vec3i.toMCVec3i() = BlockPos(x, y, z)

fun MCVec3i.toVec3(offsetCenter: Boolean = false) =
  Vec3(x, y, z).let { if (offsetCenter) it + Vec3(0.5f, 0.5f, 0.5f) else it }

fun Mat4.toMatrix4f() = Matrix4f(elements.toFloatArray())

inline fun MCWorld.toWorld(): World = QuackLibAPI.impl.getWorld(this)

inline fun MCWorldMutable.toWorld(): WorldMutable = QuackLibAPI.impl.getWorld(this)
