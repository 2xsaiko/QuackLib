package therealfarfetchd.quacklib.api.item

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

interface ItemReference {

  val mcItem: Item

  val rl: ResourceLocation

  val exists: Boolean

  fun makeStack(amount: Int = 1, meta: Int = 0): ItemStack =
    ItemStack(mcItem, amount, meta)

}