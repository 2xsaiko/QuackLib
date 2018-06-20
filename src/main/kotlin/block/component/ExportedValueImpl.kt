package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.ExportedData
import therealfarfetchd.quacklib.api.block.component.ExportedValue
import therealfarfetchd.quacklib.api.block.data.BlockDataRO

data class ExportedValueImpl<D : ExportedData<D, *>, T>(val op: (BlockDataRO) -> T) : ExportedValue<D, T>