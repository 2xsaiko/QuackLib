package therealfarfetchd.quacklib.api.render

import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty

interface ParamRenderState {

  operator fun <T> get(rp: SimpleModel.RenderParam<T>): T

  fun <T> getValue(value: RenderProperty<*, T>): T

}