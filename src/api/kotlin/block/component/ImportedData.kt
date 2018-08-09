package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.Block

fun <T> BlockComponentDataImport.import(): ImportedValue<T> =
  QuackLibAPI.impl.createImportedValueBlock(this)

@Suppress("unused")
interface ImportedValue<out T> {

  fun retrieve(data: Block): T

}