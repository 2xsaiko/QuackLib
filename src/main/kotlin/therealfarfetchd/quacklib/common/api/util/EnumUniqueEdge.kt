//package therealfarfetchd.quacklib.common.api.util
//
//import com.google.common.collect.HashBasedTable
//import net.minecraft.util.EnumFacing
//import therealfarfetchd.quacklib.common.api.extensions.mapIf
//import therealfarfetchd.quacklib.common.api.extensions.swap
//
//enum class EnumUniqueEdge(val f1: EnumFacing, val f2: EnumFacing) {
//  DownNorth(EnumFacing.DOWN, EnumFacing.NORTH),
//  DownSouth(EnumFacing.DOWN, EnumFacing.SOUTH),
//  UpNorth(EnumFacing.UP, EnumFacing.NORTH),
//  UpSouth(EnumFacing.UP, EnumFacing.SOUTH),
//  WestDown(EnumFacing.WEST, EnumFacing.DOWN),
//  WestUp(EnumFacing.WEST, EnumFacing.UP),
//  WestNorth(EnumFacing.WEST, EnumFacing.NORTH),
//  WestSouth(EnumFacing.WEST, EnumFacing.SOUTH),
//  EastDown(EnumFacing.EAST, EnumFacing.DOWN),
//  EastUp(EnumFacing.EAST, EnumFacing.UP),
//  EastNorth(EnumFacing.EAST, EnumFacing.NORTH),
//  EastSouth(EnumFacing.EAST, EnumFacing.SOUTH);
//
//  companion object {
//    val Values: List<EnumUniqueEdge> = values().toList()
//    private val Lookup: HashBasedTable<EnumFacing, EnumFacing, EnumUniqueEdge> = HashBasedTable.create()
//
//    fun fromFaces(f1: EnumFacing, f2: EnumFacing): EnumUniqueEdge {
//      if (Lookup.isEmpty) {
//        for (slot in Values) {
//          val swap = slot.f2.ordinal < slot.f1.ordinal
//          val p = (slot.f1 to slot.f2).mapIf(swap) { it.swap() }
//          Lookup.put(p.first, p.second, slot)
//        }
//      }
//      val swap = f2.ordinal < f1.ordinal
//      val p = (f1 to f2).mapIf(swap) { it.swap() }
//      return Lookup.get(p.first, p.second)
//    }
//  }
//}