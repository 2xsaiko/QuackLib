package therealfarfetchd.quacklib.api.block.init

import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty

@InitDSL
interface BlockLinkScope {

  operator fun <C : BlockComponentDataImport> C.invoke(op: C.() -> Unit)

  operator fun <M : Model> M.invoke(op: M.() -> Unit)

  // component -> component
  infix fun <T, C : BlockComponentDataExport, E : ExportedValue<C, T>, I : ImportedValue<T>> E.provides(imported: I)

  // component -> renderer
  infix fun <T, C : BlockComponentRenderProperties, E : RenderProperty<C, T>> E.provides(renderParam: SimpleModel.RenderParam<T>)

}