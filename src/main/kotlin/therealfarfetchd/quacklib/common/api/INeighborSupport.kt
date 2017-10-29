package therealfarfetchd.quacklib.common.api

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation

@FunctionalInterface
interface INeighborSupport<out T> {
  fun getNeighborAt(edge: EnumFaceLocation): T?

  fun getNeighborAt(facing: EnumFacing) = getNeighborAt(EnumFaceLocation.fromFaces(facing, null))
}

@Suppress("FunctionName")
fun <T> INeighborSupport(op: (edge: EnumFaceLocation) -> T?) = object : INeighborSupport<T> {
  override fun getNeighborAt(edge: EnumFaceLocation) = op(edge)
}