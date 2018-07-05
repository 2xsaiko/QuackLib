package therealfarfetchd.quacklib.api.tools

import net.minecraft.util.EnumFacing
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.Vec3i
import therealfarfetchd.quacklib.api.core.extensions.toVec3i

typealias PositionGrid = Vec3i
typealias Position = Vec3

fun PositionGrid.offset(facing: EnumFacing, n: Int = 1): PositionGrid =
  plus(facing.directionVec.toVec3i() * n)

fun PositionGrid.down(n: Int = 1): PositionGrid =
  copy(y = y - n)

fun PositionGrid.up(n: Int = 1): PositionGrid =
  copy(y = y + n)

fun PositionGrid.north(n: Int = 1): PositionGrid =
  copy(z = z - n)

fun PositionGrid.south(n: Int = 1): PositionGrid =
  copy(z = z + n)

fun PositionGrid.west(n: Int = 1): PositionGrid =
  copy(x = x - n)

fun PositionGrid.east(n: Int = 1): PositionGrid =
  copy(x = x + n)