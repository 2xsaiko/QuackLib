package therealfarfetchd.quacklib.api.block

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

interface BlockReference {

  val mcBlock: Block

  val rl: ResourceLocation

  fun makeStack(amount: Int = 1, meta: Int = 0): ItemStack =
    ItemStack(mcBlock, amount, meta)

}