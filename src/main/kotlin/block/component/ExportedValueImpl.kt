package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.BlockComponentDataExport
import therealfarfetchd.quacklib.api.block.component.ExportedValue
import therealfarfetchd.quacklib.api.objects.block.Block

data class ExportedValueImpl<C : BlockComponentDataExport, T>(val op: (Block) -> T) : ExportedValue<C, T>