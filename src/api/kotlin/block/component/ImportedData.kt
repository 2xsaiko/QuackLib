package therealfarfetchd.quacklib.api.block.component

import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI

abstract class ImportedData<Self : ImportedData<Self, C>, C : BlockComponentDataImport<C, Self>>(val target: C) {

  protected fun <T> import(): ImportedValue<T> =
    QuackLibAPI.impl.createImportedValue(target)

}

interface ImportedValue<out T> {

  fun retrieve(data: BlockDataRO): T

}