package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.component.*
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty

@InitDSL
interface ItemLinkScope {

  operator fun <C : ItemComponentDataImport> C.invoke(op: C.() -> Unit)

  operator fun <M : Model> M.invoke(op: M.() -> Unit)

  // component -> component
  infix fun <T, C : ItemComponentDataExport, E : ExportedValue<C, T>, I : ImportedValue<T>> E.provides(imported: I)

  // component -> renderer
  infix fun <T, C : ItemComponentRenderProperties, E : RenderProperty<C, Item, T>> E.provides(renderParam: SimpleModel.RenderParam<T>)

}