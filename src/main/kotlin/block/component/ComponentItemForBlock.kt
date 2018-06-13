package therealfarfetchd.quacklib.block.component

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.registries.GameData
import therealfarfetchd.quacklib.api.block.component.BlockComponentDrops
import therealfarfetchd.quacklib.api.block.component.BlockComponentPickBlock
import therealfarfetchd.quacklib.api.block.component.BlockData
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.item.ItemReference

class ComponentItemForBlock(val item: ItemReference) : BlockComponentDrops, BlockComponentPickBlock {

  // TODO I really want to make the resulting item a ItemBlock at some point...
  //      everything item block related kind of wants an instance of ItemBlock
  // TODO finish asm mixin thing

  override fun getDrops(data: BlockData): Set<ItemStack> = setOf(item.makeStack())

  override fun getPickBlock(data: BlockData): ItemStack = item.makeStack()

  override fun onApplied(target: BlockConfigurationScope) {
    target.item = item
  }

  override fun validate(target: BlockConfigurationScope, vc: ValidationContext) {
    if (!item.exists) {
      vc.error("Item ${item.rl} doesn't exist!")
    } else {
      val bmap: MutableMap<Block, Item> = GameData.getBlockItemMap()
      bmap[block(target.name).mcBlock] = item.mcItem
    }
  }

}