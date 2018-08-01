package therealfarfetchd.quacklib.item.render

import therealfarfetchd.quacklib.api.item.render.ItemRenderState
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty

class ItemRenderStateImpl(val item: ItemType) : ItemRenderState {

  override fun <T> get(rp: SimpleModel.RenderParam<T>): T {
    TODO("not implemented")
  }

  override fun <T> getValue(value: RenderProperty<*, T>): T {
    TODO("not implemented")
  }

}