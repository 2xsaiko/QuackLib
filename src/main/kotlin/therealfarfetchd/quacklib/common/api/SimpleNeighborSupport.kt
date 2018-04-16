package therealfarfetchd.quacklib.common.api

import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended
import therealfarfetchd.quacklib.common.api.wires.TileConnectable
import therealfarfetchd.quacklib.common.api.wires.getNeighbor

inline fun <reified T> TileConnectable.neighborSupport(crossinline filter: (EnumFacingExtended) -> Boolean, crossinline filterResult: (T) -> Boolean): INeighborSupport<T>
  = INeighborSupport { it.takeIf(filter)?.let { (getNeighbor(it) as? T).takeIf { it?.let(filterResult) ?: false } } }

inline fun <reified T> TileConnectable.neighborSupport(crossinline filter: (EnumFacingExtended) -> Boolean): INeighborSupport<T>
  = neighborSupport(filter, { true })

inline fun <reified T> TileConnectable.neighborSupport(validFaces: Collection<EnumFacingExtended>): INeighborSupport<T>
  = neighborSupport { it in validFaces }

inline fun <reified T> TileConnectable.neighborSupport(): INeighborSupport<T>
  = neighborSupport { true }

