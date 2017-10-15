package therealfarfetchd.quacklib.common.api

import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation
import therealfarfetchd.quacklib.common.api.wires.BaseConnectable

inline fun <R, reified T> R.neighborSupport(crossinline filter: (EnumFaceLocation) -> Boolean, crossinline filterResult: (T) -> Boolean): INeighborSupport<T?>
  where R : QBlock,
        R : BaseConnectable
  = INeighborSupport { it.takeIf(filter)?.let { (getNeighbor(it) as? T).takeIf { it?.let(filterResult) ?: false } } }

inline fun <R, reified T> R.neighborSupport(crossinline filter: (EnumFaceLocation) -> Boolean): INeighborSupport<T?>
  where R : QBlock,
        R : BaseConnectable
  = neighborSupport(filter, { true })

inline fun <R, reified T> R.neighborSupport(validFaces: Collection<EnumFaceLocation>): INeighborSupport<T?>
  where R : QBlock,
        R : BaseConnectable
  = neighborSupport { it in validFaces }

inline fun <R, reified T> R.neighborSupport(): INeighborSupport<T?>
  where R : QBlock,
        R : BaseConnectable
  = neighborSupport { true }