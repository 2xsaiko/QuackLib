package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType

interface ItemInitializationContext {

  fun addItem(name: String, op: ItemConfigurationScope.() -> Unit = {}): ItemType

  fun addPlacementItem(block: BlockType, name: String = block.registryName.resourcePath, op: ItemConfigurationScope.() -> Unit = {}): ItemType

  fun addTab(name: String, icon: ItemType, op: TabConfigurationScope.() -> Unit = {})

}