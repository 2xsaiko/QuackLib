package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.ImportedValue
import therealfarfetchd.quacklib.api.objects.block.Block

class ImportedValueImpl<T> : ImportedValue<T> {

  lateinit var export: ExportedValueImpl<*, T>

  override fun retrieve(data: Block): T {
    if (!::export.isInitialized) error("Accessing unbound value!")

    return export.op(data)
  }

}