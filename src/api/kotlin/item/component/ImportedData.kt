package therealfarfetchd.quacklib.api.item.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.item.Item

fun <T> ItemComponentDataImport.import(): ImportedValue<T> =
  QuackLibAPI.impl.createImportedValueItem(this)

@Suppress("unused")
interface ImportedValue<out T> {

  fun retrieve(data: Item): T

}