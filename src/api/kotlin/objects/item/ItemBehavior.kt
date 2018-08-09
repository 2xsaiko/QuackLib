package therealfarfetchd.quacklib.api.objects.item

import therealfarfetchd.quacklib.api.item.component.ImportedValue
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.item.data.PartAccessToken

interface ItemBehavior {

  fun <T : ItemDataPart> getPart(item: Item, token: PartAccessToken<T>): T

  fun <T> getImported(item: Item, value: ImportedValue<T>): T

}