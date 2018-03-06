package therealfarfetchd.quacklib.common.api.util

import net.minecraft.util.EnumFacing

enum class EnumFacingExtended(
  /**
   * The part of the block space this facing is in. null is center
   */
  val part: EnumFacing?,

  /**
   * The direction this facing points at.
   */
  val direction: EnumFacing
) {
  CenterDown(null, EnumFacing.DOWN),
  CenterUp(null, EnumFacing.UP),
  CenterNorth(null, EnumFacing.NORTH),
  CenterSouth(null, EnumFacing.SOUTH),
  CenterWest(null, EnumFacing.WEST),
  CenterEast(null, EnumFacing.EAST),

  DownDown(EnumFacing.DOWN, EnumFacing.DOWN),
  DownUp(EnumFacing.DOWN, EnumFacing.UP),
  DownNorth(EnumFacing.DOWN, EnumFacing.NORTH),
  DownSouth(EnumFacing.DOWN, EnumFacing.SOUTH),
  DownWest(EnumFacing.DOWN, EnumFacing.WEST),
  DownEast(EnumFacing.DOWN, EnumFacing.EAST),

  UpDown(EnumFacing.UP, EnumFacing.DOWN),
  UpUp(EnumFacing.UP, EnumFacing.UP),
  UpNorth(EnumFacing.UP, EnumFacing.NORTH),
  UpSouth(EnumFacing.UP, EnumFacing.SOUTH),
  UpWest(EnumFacing.UP, EnumFacing.WEST),
  UpEast(EnumFacing.UP, EnumFacing.EAST),

  NorthDown(EnumFacing.NORTH, EnumFacing.DOWN),
  NorthUp(EnumFacing.NORTH, EnumFacing.UP),
  NorthNorth(EnumFacing.NORTH, EnumFacing.NORTH),
  NorthSouth(EnumFacing.NORTH, EnumFacing.SOUTH),
  NorthWest(EnumFacing.NORTH, EnumFacing.WEST),
  NorthEast(EnumFacing.NORTH, EnumFacing.EAST),

  SouthDown(EnumFacing.SOUTH, EnumFacing.DOWN),
  SouthUp(EnumFacing.SOUTH, EnumFacing.UP),
  SouthNorth(EnumFacing.SOUTH, EnumFacing.NORTH),
  SouthSouth(EnumFacing.SOUTH, EnumFacing.SOUTH),
  SouthWest(EnumFacing.SOUTH, EnumFacing.WEST),
  SouthEast(EnumFacing.SOUTH, EnumFacing.EAST),

  WestDown(EnumFacing.WEST, EnumFacing.DOWN),
  WestUp(EnumFacing.WEST, EnumFacing.UP),
  WestNorth(EnumFacing.WEST, EnumFacing.NORTH),
  WestSouth(EnumFacing.WEST, EnumFacing.SOUTH),
  WestWest(EnumFacing.WEST, EnumFacing.WEST),
  WestEast(EnumFacing.WEST, EnumFacing.EAST),

  EastDown(EnumFacing.EAST, EnumFacing.DOWN),
  EastUp(EnumFacing.EAST, EnumFacing.UP),
  EastNorth(EnumFacing.EAST, EnumFacing.NORTH),
  EastSouth(EnumFacing.EAST, EnumFacing.SOUTH),
  EastWest(EnumFacing.EAST, EnumFacing.WEST),
  EastEast(EnumFacing.EAST, EnumFacing.EAST),
  ;

  operator fun component1(): EnumFacing? = part
  operator fun component2(): EnumFacing = direction

  val pointsDown = part == direction
  val pointsUp = part == direction.opposite
  val isVertical = pointsDown || pointsUp

  val oppositeExternal by lazy {
    when {
      part == null -> fromFaces(direction, direction.opposite)
      pointsUp     -> fromFaces(part.opposite, direction.opposite)
      pointsDown   -> fromFaces(part.opposite, direction.opposite)
      else         -> fromFaces(part, direction.opposite)
    }
  }

  val oppositeCorner by lazy {
    when {
      isVertical || part == null -> null
      else                       -> fromFaces(direction.opposite, part.opposite)
    }
  }

  val oppositeInternal by lazy {
    when {
      part == null -> fromFaces(direction, direction.opposite)
      pointsUp     -> fromFaces(null, direction.opposite)
      pointsDown   -> null
      else         -> fromFaces(direction, part)
    }
  }

  companion object {
    val Values: List<EnumFacingExtended> = values().toList()
    private var Lookup: Map<Pair<EnumFacing?, EnumFacing>, EnumFacingExtended> = emptyMap()

    @JvmStatic
    fun fromFaces(part: EnumFacing?, direction: EnumFacing): EnumFacingExtended {
      if (Lookup.isEmpty()) {
        for (slot in Values) {
          Lookup += slot.part to slot.direction to slot
        }
      }
      return Lookup[part to direction]!!
    }
  }
}