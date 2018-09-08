package therealfarfetchd.quacklib.common.api.util

import com.google.common.collect.HashBasedTable
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*

enum class EnumEdge(val base: EnumFacing, val side: EnumFacing) {
  DownNorth(DOWN, NORTH),
  DownSouth(DOWN, SOUTH),
  DownWest(DOWN, WEST),
  DownEast(DOWN, EAST),
  UpNorth(UP, NORTH),
  UpSouth(UP, SOUTH),
  UpWest(UP, WEST),
  UpEast(UP, EAST),
  NorthDown(NORTH, DOWN),
  NorthUp(NORTH, UP),
  NorthWest(NORTH, WEST),
  NorthEast(NORTH, EAST),
  SouthDown(SOUTH, DOWN),
  SouthUp(SOUTH, UP),
  SouthWest(SOUTH, WEST),
  SouthEast(SOUTH, EAST),
  WestDown(WEST, DOWN),
  WestUp(WEST, UP),
  WestNorth(WEST, NORTH),
  WestSouth(WEST, SOUTH),
  EastDown(EAST, DOWN),
  EastUp(EAST, UP),
  EastNorth(EAST, NORTH),
  EastSouth(EAST, SOUTH);

  companion object {
    val Values: List<EnumEdge> = values().toList()
    private val Lookup: HashBasedTable<EnumFacing, EnumFacing, EnumEdge> = HashBasedTable.create()

    fun fromFaces(base: EnumFacing, side: EnumFacing): EnumEdge {
      if (Lookup.isEmpty) {
        for (slot in Values) {
          Lookup.put(slot.base, slot.side, slot)
        }
      }
      if (!Lookup.contains(base, side)) return DownNorth
      return Lookup.get(base, side)
    }
  }
}
