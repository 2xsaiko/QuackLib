package therealfarfetchd.quacklib.objects.item

import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.item.component.ImportedValue
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.item.data.PartAccessToken
import therealfarfetchd.quacklib.api.objects.getComponentsOfType
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemBehavior
import therealfarfetchd.quacklib.api.objects.item.ItemType

class StandardItemBehavior(val type: ItemType) : ItemBehavior {

  override fun <T : ItemDataPart> getPart(item: Item, token: PartAccessToken<T>): T {
    TODO("not implemented")
  }

  override fun <T> getImported(item: Item, value: ImportedValue<T>): T {
    return value.retrieve(item)
  }

  private inline fun <reified T : BlockComponent> getComponentsOfType() =
    type.getComponentsOfType<T>()

}