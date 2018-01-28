package therealfarfetchd.quacklib.common.api.item

import mcmultipart.api.item.ItemBlockMultipart
import mcmultipart.api.multipart.IMultipart
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class ItemDelegated : Item(), IItemSpecialHook {
  override fun getUnlocalizedName(stack: ItemStack) =
    super.getUnlocalizedName(stack) + getUnlocalizedNameSuffix(stack)

  override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) = getSubItemsH(tab, items)
}

open class ItemBlockDelegated(block: Block) : ItemBlock(block), IItemSpecialHook {
  override fun getUnlocalizedName(stack: ItemStack) =
    super.getUnlocalizedName(stack) + getUnlocalizedNameSuffix(stack)

  override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) = getSubItemsH(tab, items)
}

open class ItemBlockMultipartDelegated(block: Block, multipartBlock: IMultipart) : ItemBlockMultipart(block, multipartBlock), IItemSpecialHook {
  override fun getUnlocalizedName(stack: ItemStack) =
    super.getUnlocalizedName(stack) + getUnlocalizedNameSuffix(stack)

  override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) = getSubItemsH(tab, items)
}