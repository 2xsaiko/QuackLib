package therealfarfetchd.quacklib.item.init

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.component.*
import therealfarfetchd.quacklib.api.item.init.ItemLinkScope
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.item.component.ExportedValueImpl
import therealfarfetchd.quacklib.item.component.ImportedValueImpl
import therealfarfetchd.quacklib.render.property.RenderPropertyItemImpl

class ItemLinkScopeImpl(val rl: ResourceLocation) : ItemLinkScope {

  override fun <C : ItemComponentDataImport> C.invoke(op: C.() -> Unit) = with(this, op)

  @Suppress("UNCHECKED_CAST")
  override fun <T, C : ItemComponentDataExport, E : ExportedValue<C, T>, I : ImportedValue<T>> E.provides(imported: I) {
    this as ExportedValueImpl<C, T>
    imported as ImportedValueImpl<T>

    imported.export = this
  }

  override fun <R : Model> R.invoke(op: R.() -> Unit) = with(this, op)

  @Suppress("UNCHECKED_CAST")
  override fun <T, C : ItemComponentRenderProperties, E : RenderProperty<C, Item, T>> E.provides(renderParam: SimpleModel.RenderParam<T>) {
    this as RenderPropertyItemImpl<C, T>

    renderParam.setImplItem(this@ItemLinkScopeImpl.rl, this)
  }

}