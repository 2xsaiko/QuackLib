package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.Block

@Suppress("unused")
abstract class ExportedData<Self : ExportedData<Self, C>, C : BlockComponentDataExport<C, Self>>(val target: C)

@Suppress("unused")
interface ExportedValue<D : ExportedData<D, *>, out T>

fun <P : BlockComponentDataExport<P, T>, R, T : ExportedData<T, P>> T.export(op: (P, Block) -> R): ExportedValue<T, R> =
  QuackLibAPI.impl.createExportedValue(target, op)

inline fun <P, D : BlockDataPart, R, T : ExportedData<T, P>> T.export(crossinline op: (D) -> R): ExportedValue<T, R>
  where P : BlockComponentDataExport<P, T>,
        P : BlockComponentData<D> =
  export { component, data -> op(data[component.part]) }