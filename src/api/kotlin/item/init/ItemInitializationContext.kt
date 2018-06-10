package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.item.ItemReference

interface ItemInitializationContext {

  fun addItem(name: String, op: ItemConfigurationScope.() -> Unit = {})

  fun addTab(name: String, icon: ItemReference, op: TabConfigurationScope.() -> Unit = {})

}