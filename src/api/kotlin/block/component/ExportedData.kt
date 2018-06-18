package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.api.block.data.get
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI

@Suppress("unused")
abstract class ExportedData<Self : ExportedData<Self, C>, C : BlockComponentDataExport<C, Self>>(val target: C)

@Suppress("unused")
interface ExportedValue<P : ExportedData<*, *>, out T>

fun <P : BlockComponentDataExport<P, T>, R, T : ExportedData<T, P>> T.export(op: (P, BlockDataRO) -> R): ExportedValue<T, R> =
  QuackLibAPI.impl.createProvidedValue(this, op)

inline fun <P, D : BlockDataPart, R, T : ExportedData<T, P>> T.export(crossinline op: (D) -> R): ExportedValue<T, R>
  where P : BlockComponentDataExport<P, T>,
        P : BlockComponentData<D> =
  export { component, data -> op(data[component.part]) }