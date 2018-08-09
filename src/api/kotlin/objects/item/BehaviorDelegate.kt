package therealfarfetchd.quacklib.api.objects.item

import therealfarfetchd.quacklib.api.item.component.ImportedValue
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.item.data.PartAccessToken

interface BehaviorDelegate {

  val item: Item

  val behavior: ItemBehavior

  operator fun <T : ItemDataPart> get(token: PartAccessToken<T>): T =
    behavior.getPart(item, token)

  operator fun <T> get(value: ImportedValue<T>): T =
    behavior.getImported(item, value)


}