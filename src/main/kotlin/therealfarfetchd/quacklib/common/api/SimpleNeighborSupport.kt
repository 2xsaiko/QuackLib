package therealfarfetchd.quacklib.common.api

import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended
import therealfarfetchd.quacklib.common.api.wires.BaseConnectable2
import therealfarfetchd.quacklib.common.api.wires.getNeighbor

inline fun <R, reified T> R.neighborSupport(crossinline filter: (EnumFacingExtended) -> Boolean, crossinline filterResult: (T) -> Boolean): INeighborSupport<T>
  where R : QBlock,
        R : BaseConnectable2
  = INeighborSupport { it.takeIf(filter)?.let { (getNeighbor(it) as? T).takeIf { it?.let(filterResult) ?: false } } }

inline fun <R, reified T> R.neighborSupport(crossinline filter: (EnumFacingExtended) -> Boolean): INeighborSupport<T>
  where R : QBlock,
        R : BaseConnectable2
  = neighborSupport(filter, { true })

inline fun <R, reified T> R.neighborSupport(validFaces: Collection<EnumFacingExtended>): INeighborSupport<T>
  where R : QBlock,
        R : BaseConnectable2
  = neighborSupport { it in validFaces }

inline fun <R, reified T> R.neighborSupport(): INeighborSupport<T>
  where R : QBlock,
        R : BaseConnectable2
  = neighborSupport { true }