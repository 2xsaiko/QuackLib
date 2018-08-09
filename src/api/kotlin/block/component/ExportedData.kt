package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.Block

@Suppress("unused")
interface ExportedValue<C : BlockComponentDataExport, out T>

@Suppress("NOTHING_TO_INLINE")
inline fun <C : BlockComponentDataExport, R> C.export(noinline op: (C, Block) -> R): ExportedValue<C, R> =
  QuackLibAPI.impl.createExportedValueBlock(this, op)

inline fun <C, D : BlockDataPart, R> C.export(crossinline op: (D) -> R): ExportedValue<C, R>
  where C : BlockComponentDataExport,
        C : BlockComponentData<D> =
  export { _, data -> op(data[part]) }