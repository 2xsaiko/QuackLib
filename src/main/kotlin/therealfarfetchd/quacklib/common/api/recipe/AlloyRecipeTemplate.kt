package therealfarfetchd.quacklib.common.api.recipe

import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary
import therealfarfetchd.quacklib.common.api.extensions.makeStack

class AlloyRecipeTemplate {
  var inputs: List<ItemTemplate> = emptyList()

  var output: ItemTemplate = ItemStackTemplate(Items.AIR, 1, 0)

  fun stack(item: Item, count: Int = 1, meta: Int = 0): ItemTemplate = ItemStackTemplate(item, count, meta)

  fun stack(block: Block, count: Int = 1, meta: Int = 0): ItemTemplate = ItemStackTemplate(Item.getItemFromBlock(block), count, meta)

  fun oredict(item: String, count: Int = 1): ItemTemplate = OreDictTemplate(item, count)

  fun isValid(): Boolean {
    return inputs.isNotEmpty() && inputs.all { it.isValid() } && output.makeStack().item != Items.AIR
  }
}

interface ItemTemplate {
  fun isValid(): Boolean
  fun isSameItem(stack: ItemStack): Boolean
  fun makeStack(): ItemStack
}

data class ItemStackTemplate(val item: Item, val count: Int, val meta: Int) : ItemTemplate {
  override fun isValid(): Boolean = count > 0

  override fun isSameItem(stack: ItemStack): Boolean {
    return stack.isItemEqual(makeStack())
  }

  override fun makeStack(): ItemStack {
    if (!isValid()) error("Invalid item!")
    return item.makeStack(count, meta)
  }

  override fun toString() = "${item.registryName}@${meta}x$count"
}

data class OreDictTemplate(val item: String, val count: Int) : ItemTemplate {
  private val stack by lazy {
    if (!isValid()) error("Invalid item!")
    OreDictionary.getOres(item)[0].copy().also { it.count = count }
  }

  override fun isValid(): Boolean {
    return count > 0 && OreDictionary.doesOreNameExist(item)
  }

  override fun isSameItem(stack: ItemStack): Boolean {
    return OreDictionary.containsMatch(true, OreDictionary.getOres(item), stack)
  }

  override fun makeStack(): ItemStack = stack.copy()

  override fun toString() = "${item}x$count"
}