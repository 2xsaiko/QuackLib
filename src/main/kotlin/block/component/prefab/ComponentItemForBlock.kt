package therealfarfetchd.quacklib.block.component.prefab

import therealfarfetchd.quacklib.api.block.component.BlockComponentDrops
import therealfarfetchd.quacklib.api.block.component.BlockComponentInternal
import therealfarfetchd.quacklib.api.block.component.BlockComponentPickBlock
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType

class ComponentItemForBlock(val item: ItemType) : BlockComponentInternal, BlockComponentDrops, BlockComponentPickBlock {

  // TODO I really want to make the resulting item a ItemBlock at some point...
  //      everything item block related kind of wants an instance of ItemBlock
  // TODO finish asm mixin thing

  override fun getDrops(data: Block): Set<Item> = setOf(item.create())

  override fun getPickBlock(data: Block): Item = item.create()

  override fun onApplied(target: BlockConfigurationScope) {
    target.item = item
  }

  override fun validate(target: BlockConfigurationScope, vc: ValidationContext) {
    // TODO this doesn't work because changes to the map aren't saved

    // val bmap: MutableMap<MCBlock, MCItem> = GameData.getBlockItemMap()
    // bmap[block(target.name).mcBlock] = unsafe { item.mc }
  }

}