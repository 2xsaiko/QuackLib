package therealfarfetchd.quacklib.block.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.init.BlockLinkScope
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.block.component.ExportedValueImpl
import therealfarfetchd.quacklib.block.component.ImportedValueImpl
import therealfarfetchd.quacklib.render.property.RenderPropertyBlockImpl

class BlockLinkScopeImpl(val rl: ResourceLocation) : BlockLinkScope {

  override fun <C : BlockComponentDataImport> C.invoke(op: C.() -> Unit) = with(this, op)

  @Suppress("UNCHECKED_CAST")
  override fun <T, C : BlockComponentDataExport, E : ExportedValue<C, T>, I : ImportedValue<T>> E.provides(imported: I) {
    this as ExportedValueImpl<C, T>
    imported as ImportedValueImpl<T>

    imported.export = this
  }

  override fun <R : Model> R.invoke(op: R.() -> Unit) = with(this, op)

  @Suppress("UNCHECKED_CAST")
  override fun <T, C : BlockComponentRenderProperties, E : RenderProperty<C, Block, T>> E.provides(renderParam: SimpleModel.RenderParam<T>) {
    this as RenderPropertyBlockImpl<C, T>

    renderParam.setImplBlock(this@BlockLinkScopeImpl.rl, this)
  }
}