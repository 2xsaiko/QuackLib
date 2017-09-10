package therealfarfetchd.quacklib.common.util

import net.minecraft.util.EnumFacing

enum class EnumFaceLocation(val base: EnumFacing, val side: EnumFacing?) {
  DownCenter(EnumFacing.DOWN, null),
  DownNorth(EnumFacing.DOWN, EnumFacing.NORTH),
  DownSouth(EnumFacing.DOWN, EnumFacing.SOUTH),
  DownWest(EnumFacing.DOWN, EnumFacing.WEST),
  DownEast(EnumFacing.DOWN, EnumFacing.EAST),
  UpCenter(EnumFacing.UP, null),
  UpNorth(EnumFacing.UP, EnumFacing.NORTH),
  UpSouth(EnumFacing.UP, EnumFacing.SOUTH),
  UpWest(EnumFacing.UP, EnumFacing.WEST),
  UpEast(EnumFacing.UP, EnumFacing.EAST),
  NorthCenter(EnumFacing.NORTH, null),
  NorthDown(EnumFacing.NORTH, EnumFacing.DOWN),
  NorthUp(EnumFacing.NORTH, EnumFacing.UP),
  NorthWest(EnumFacing.NORTH, EnumFacing.WEST),
  NorthEast(EnumFacing.NORTH, EnumFacing.EAST),
  SouthCenter(EnumFacing.SOUTH, null),
  SouthDown(EnumFacing.SOUTH, EnumFacing.DOWN),
  SouthUp(EnumFacing.SOUTH, EnumFacing.UP),
  SouthWest(EnumFacing.SOUTH, EnumFacing.WEST),
  SouthEast(EnumFacing.SOUTH, EnumFacing.EAST),
  WestCenter(EnumFacing.WEST, null),
  WestDown(EnumFacing.WEST, EnumFacing.DOWN),
  WestUp(EnumFacing.WEST, EnumFacing.UP),
  WestNorth(EnumFacing.WEST, EnumFacing.NORTH),
  WestSouth(EnumFacing.WEST, EnumFacing.SOUTH),
  EastCenter(EnumFacing.EAST, null),
  EastDown(EnumFacing.EAST, EnumFacing.DOWN),
  EastUp(EnumFacing.EAST, EnumFacing.UP),
  EastNorth(EnumFacing.EAST, EnumFacing.NORTH),
  EastSouth(EnumFacing.EAST, EnumFacing.SOUTH);

  operator fun component1(): EnumFacing = base
  operator fun component2(): EnumFacing? = side

  companion object {
    val Values: List<EnumFaceLocation> = values().toList()
    private var Lookup: Map<Pair<EnumFacing, EnumFacing?>, EnumFaceLocation> = emptyMap()

    @JvmStatic
    fun fromFaces(base: EnumFacing, side: EnumFacing?): EnumFaceLocation {
      if (Lookup.isEmpty()) {
        for (slot in Values) {
          Lookup += slot.base to slot.side to slot
        }
      }
      return Lookup[base to side]!!
    }
  }
}