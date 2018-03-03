package therealfarfetchd.quacklib.common.api

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended

@FunctionalInterface
interface INeighborSupport<out T> {
  fun getNeighborAt(edge: EnumFacingExtended): T?

  fun getNeighborAt(facing: EnumFacing) = getNeighborAt(EnumFacingExtended.fromFaces(null, facing))
}

@Suppress("FunctionName")
fun <T> INeighborSupport(op: (edge: EnumFacingExtended) -> T?) = object : INeighborSupport<T> {
  override fun getNeighborAt(edge: EnumFacingExtended) = op(edge)
}