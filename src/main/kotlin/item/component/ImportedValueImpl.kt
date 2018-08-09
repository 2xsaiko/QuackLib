package therealfarfetchd.quacklib.item.component

import therealfarfetchd.quacklib.api.item.component.ImportedValue
import therealfarfetchd.quacklib.api.objects.item.Item

class ImportedValueImpl<T> : ImportedValue<T> {

  lateinit var export: ExportedValueImpl<*, T>

  override fun retrieve(data: Item): T {
    if (!::export.isInitialized) error("Accessing unbound value!")

    return export.op(data)
  }

}