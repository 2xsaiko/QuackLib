package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.item.ItemReference

interface ItemInitializationContext {

  fun addItem(name: String, op: ItemConfigurationScope.() -> Unit = {}): ItemReference

  fun addPlacementItem(block: BlockReference, name: String = block.rl.resourcePath, op: ItemConfigurationScope.() -> Unit = {}): ItemReference

  fun addTab(name: String, icon: ItemReference, op: TabConfigurationScope.() -> Unit = {})

}