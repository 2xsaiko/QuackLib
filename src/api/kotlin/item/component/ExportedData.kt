package therealfarfetchd.quacklib.api.item.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.objects.item.Item

@Suppress("unused")
interface ExportedValue<C : ItemComponentDataExport, out T>

@Suppress("NOTHING_TO_INLINE")
inline fun <C : ItemComponentDataExport, R> C.export(noinline op: (C, Item) -> R): ExportedValue<C, R> =
  QuackLibAPI.impl.createExportedValueItem(this, op)

inline fun <C, D : ItemDataPart, R> C.export(crossinline op: (D) -> R): ExportedValue<C, R>
  where C : ItemComponentDataExport,
        C : ItemComponentData<D> =
  export { _, data -> op(data[part]) }