package therealfarfetchd.quacklib.api.block.redstone

import net.minecraft.util.EnumFacing

enum class ConnectionMask(val face: EnumFacing, val f2: EnumFacing?) {

  DOWN_CENTER(EnumFacing.DOWN, null),
  DOWN_NORTH(EnumFacing.DOWN, EnumFacing.NORTH),
  DOWN_SOUTH(EnumFacing.DOWN, EnumFacing.SOUTH),
  DOWN_WEST(EnumFacing.DOWN, EnumFacing.WEST),
  DOWN_EAST(EnumFacing.DOWN, EnumFacing.EAST),

  UP_CENTER(EnumFacing.UP, null),
  UP_NORTH(EnumFacing.UP, EnumFacing.NORTH),
  UP_SOUTH(EnumFacing.UP, EnumFacing.SOUTH),
  UP_WEST(EnumFacing.UP, EnumFacing.WEST),
  UP_EAST(EnumFacing.UP, EnumFacing.EAST),

  NORTH_CENTER(EnumFacing.NORTH, null),
  NORTH_DOWN(EnumFacing.NORTH, EnumFacing.DOWN),
  NORTH_UP(EnumFacing.NORTH, EnumFacing.UP),
  NORTH_WEST(EnumFacing.NORTH, EnumFacing.WEST),
  NORTH_EAST(EnumFacing.NORTH, EnumFacing.EAST),

  SOUTH_CENTER(EnumFacing.SOUTH, null),
  SOUTH_DOWN(EnumFacing.SOUTH, EnumFacing.DOWN),
  SOUTH_UP(EnumFacing.SOUTH, EnumFacing.UP),
  SOUTH_WEST(EnumFacing.SOUTH, EnumFacing.WEST),
  SOUTH_EAST(EnumFacing.SOUTH, EnumFacing.EAST),

  WEST_CENTER(EnumFacing.WEST, null),
  WEST_DOWN(EnumFacing.WEST, EnumFacing.DOWN),
  WEST_UP(EnumFacing.WEST, EnumFacing.UP),
  WEST_NORTH(EnumFacing.WEST, EnumFacing.NORTH),
  WEST_SOUTH(EnumFacing.WEST, EnumFacing.SOUTH),

  EAST_CENTER(EnumFacing.EAST, null),
  EAST_DOWN(EnumFacing.EAST, EnumFacing.DOWN),
  EAST_UP(EnumFacing.EAST, EnumFacing.UP),
  EAST_NORTH(EnumFacing.EAST, EnumFacing.NORTH),
  EAST_SOUTH(EnumFacing.EAST, EnumFacing.SOUTH);

  val opposite: ConnectionMask by lazy { opposites.getValue(this) }

  companion object {

    val Values = values().toList()

    private val opposites = Values.associateBy { fromFace(it.face.opposite, it.f2) }

    fun fromFace(face: EnumFacing, f2: EnumFacing?): ConnectionMask {
      val i = 5 * face.index + (f2?.index?.plus(1) ?: 0)
      return Values[i]
    }

  }

}