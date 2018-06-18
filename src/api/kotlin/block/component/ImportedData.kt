package therealfarfetchd.quacklib.api.block.component

abstract class ImportedData<Self : ImportedData<Self, C>, C : BlockComponentDataImport<C, Self>>(val target: C) {

  protected fun <T> import(): ImportedValue<T> = TODO()

}

interface ImportedValue<out T>