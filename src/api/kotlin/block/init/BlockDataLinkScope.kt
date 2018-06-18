package therealfarfetchd.quacklib.api.block.init

import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.core.init.InitDSL

@InitDSL
interface BlockDataLinkScope {

  operator fun <T : BlockComponentDataImport<T, D>, D : ImportedData<D, T>> AppliedComponent<T>.invoke(op: D.() -> Unit)

  val <T : BlockComponentDataExport<T, D>, D : ExportedData<D, T>> AppliedComponent<T>.exports: D

  infix fun <T, E : ExportedValue<*, T>, I : ImportedValue<T>> E.provides(imported: I)

}