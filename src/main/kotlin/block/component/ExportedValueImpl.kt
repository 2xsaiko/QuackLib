package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.BlockComponentDataExport
import therealfarfetchd.quacklib.api.block.component.ExportedData
import therealfarfetchd.quacklib.api.block.component.ExportedValue
import therealfarfetchd.quacklib.api.block.data.BlockDataRO

data class ExportedValueImpl<D : BlockComponentDataExport<D, P>, P : ExportedData<P, D>, T>(val target: P, val op: (D, BlockDataRO) -> T) : ExportedValue<P, T>