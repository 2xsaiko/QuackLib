package therealfarfetchd.quacklib.item.render

import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.api.item.render.ItemRenderState
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.objects.item.ItemImpl

data class ItemRenderStateImpl(val item: ItemType, val stack: ItemStack) : ItemRenderState {

  override fun <T> get(rp: SimpleModel.RenderParam<T>): T {
    return rp.getValueItem(item, this)
  }

  override fun <T> getValue(value: RenderProperty<*, Item, T>): T {
    return value.getValue(ItemImpl(item, stack))
  }

}