package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.ExportedData
import therealfarfetchd.quacklib.api.block.component.ExportedValue
import therealfarfetchd.quacklib.api.objects.block.Block

data class ExportedValueImpl<D : ExportedData<D, *>, T>(val op: (Block) -> T) : ExportedValue<D, T>