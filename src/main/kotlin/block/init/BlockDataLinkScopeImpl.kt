package therealfarfetchd.quacklib.block.init

import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.init.BlockDataLinkScope
import therealfarfetchd.quacklib.block.component.ExportedValueImpl
import therealfarfetchd.quacklib.block.component.ImportedValueImpl

class BlockDataLinkScopeImpl : BlockDataLinkScope {

  override fun <T : BlockComponentDataImport<T, D>, D : ImportedData<D, T>> AppliedComponent<T>.invoke(op: D.() -> Unit) {
    with(instance.imported, op)
  }

  override val <T : BlockComponentDataExport<T, D>, D : ExportedData<D, T>> AppliedComponent<T>.exports: D
    get() = instance.exported

  @Suppress("UNCHECKED_CAST")
  override fun <T, C : BlockComponentDataExport<C, D>, D : ExportedData<D, C>, E : ExportedValue<D, T>, I : ImportedValue<T>> E.provides(imported: I) {
    this as ExportedValueImpl<D, T>
    imported as ImportedValueImpl<T>

    imported.export = this
  }

}