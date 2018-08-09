package therealfarfetchd.quacklib.objects.item

import therealfarfetchd.quacklib.api.item.component.ImportedValue
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.item.data.PartAccessToken
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemBehavior
import therealfarfetchd.quacklib.api.objects.item.MCItemType

class VanillaItemBehavior(val type: MCItemType) : ItemBehavior {

  override fun <T : ItemDataPart> getPart(item: Item, token: PartAccessToken<T>): T {
    TODO("not implemented")
  }

  override fun <T> getImported(item: Item, value: ImportedValue<T>): T {
    TODO("not implemented")
  }

}