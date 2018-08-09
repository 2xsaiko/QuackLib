package therealfarfetchd.quacklib.item.component

import therealfarfetchd.quacklib.api.item.component.ExportedValue
import therealfarfetchd.quacklib.api.item.component.ItemComponentDataExport
import therealfarfetchd.quacklib.api.objects.item.Item

data class ExportedValueImpl<C : ItemComponentDataExport, T>(val op: (Item) -> T) : ExportedValue<C, T>

