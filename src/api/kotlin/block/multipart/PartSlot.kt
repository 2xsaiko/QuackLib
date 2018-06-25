package therealfarfetchd.quacklib.api.block.multipart

import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.Axis

enum class PartSlot(
  val f1: EnumFacing? = null,
  val f2: EnumFacing? = null,
  val f3: EnumFacing? = null,
  val axis: Axis? = null
) {
  CENTER,

  DOWN(EnumFacing.DOWN),
  UP(EnumFacing.UP),
  NORTH(EnumFacing.NORTH),
  SOUTH(EnumFacing.SOUTH),
  WEST(EnumFacing.WEST),
  EAST(EnumFacing.EAST),

  EDGE_XNN(EnumFacing.DOWN, EnumFacing.NORTH, axis = Axis.X),
  EDGE_XNP(EnumFacing.DOWN, EnumFacing.SOUTH, axis = Axis.X),
  EDGE_XPN(EnumFacing.UP, EnumFacing.NORTH, axis = Axis.X),
  EDGE_XPP(EnumFacing.UP, EnumFacing.SOUTH, axis = Axis.X),
  EDGE_NYN(EnumFacing.WEST, EnumFacing.NORTH, axis = Axis.Y),
  EDGE_NYP(EnumFacing.WEST, EnumFacing.SOUTH, axis = Axis.Y),
  EDGE_PYN(EnumFacing.EAST, EnumFacing.NORTH, axis = Axis.Y),
  EDGE_PYP(EnumFacing.EAST, EnumFacing.SOUTH, axis = Axis.Y),
  EDGE_NNZ(EnumFacing.WEST, EnumFacing.DOWN, axis = Axis.Z),
  EDGE_NPZ(EnumFacing.WEST, EnumFacing.UP, axis = Axis.Z),
  EDGE_PNZ(EnumFacing.EAST, EnumFacing.DOWN, axis = Axis.Z),
  EDGE_PPZ(EnumFacing.EAST, EnumFacing.UP, axis = Axis.Z),

  CORNER_NNN(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.NORTH),
  CORNER_NNP(EnumFacing.WEST, EnumFacing.DOWN, EnumFacing.SOUTH),
  CORNER_NPN(EnumFacing.WEST, EnumFacing.UP, EnumFacing.NORTH),
  CORNER_NPP(EnumFacing.WEST, EnumFacing.UP, EnumFacing.SOUTH),
  CORNER_PNN(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.NORTH),
  CORNER_PNP(EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.SOUTH),
  CORNER_PPN(EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH),
  CORNER_PPP(EnumFacing.EAST, EnumFacing.UP, EnumFacing.SOUTH);

  companion object {
    val Values = values().toList()

    private val edges = Values
      .filter { it.f1 != null && it.f2 != null && it.f3 == null && it.axis != null }
      .associateBy { Triple(it.f1!!, it.f2!!, it.axis!!) }

    private val corners = Values
      .filter { it.f1 != null && it.f2 != null && it.f3 != null && it.axis == null }
      .associateBy { Triple(it.f1!!, it.f2!!, it.f3!!) }

    fun getFace(facing: EnumFacing): PartSlot = Values[1 + facing.ordinal]

    fun getEdge(axis: Axis, f1: EnumFacing, f2: EnumFacing) =
      edges.getValue(Triple(f1, f2, axis))

    fun getCorner(f1: EnumFacing, f2: EnumFacing, f3: EnumFacing) =
      corners.getValue(Triple(f1, f2, f3))
  }

}