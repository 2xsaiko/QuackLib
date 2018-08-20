@file:Suppress("NOTHING_TO_INLINE")

package therealfarfetchd.quacklib.api.core.extensions

import net.minecraft.util.EnumFacing
import therealfarfetchd.math.Vec3i

inline fun Vec3i.down(n: Int = 1) =
  copy(y = y - n)

inline fun Vec3i.up(n: Int = 1) =
  copy(y = y + n)

inline fun Vec3i.north(n: Int = 1) =
  copy(z = z - n)

inline fun Vec3i.south(n: Int = 1) =
  copy(z = z + n)

inline fun Vec3i.west(n: Int = 1) =
  copy(x = x - n)

inline fun Vec3i.east(n: Int = 1) =
  copy(x = x + n)

inline fun EnumFacing.toVec3i(): Vec3i =
  directionVec.toVec3i()