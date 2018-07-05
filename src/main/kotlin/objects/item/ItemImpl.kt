package therealfarfetchd.quacklib.objects.item

import net.minecraft.item.ItemStack
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItem

class ItemImpl(
  override val type: ItemType,
  val stack: ItemStack
) : Item {

  constructor(type: ItemType, amount: Int, meta: Int) : this(type, ItemStack(unsafe { type.mc }, amount, meta))

  constructor(type: ItemType) : this(type, 1, 0)

  constructor(item: MCItem) : this(ItemTypeImpl.getItem(item.item), item)

  override var count: Int
    get() = stack.count
    set(value) {
      stack.count = value
    }

  override val Unsafe.mc: MCItem
    get() = stack

}