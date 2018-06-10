package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.item.ItemReference

interface ItemInitializationContext {

  fun item(name: String, op: ItemConfigurationScope.() -> Unit)

  fun tab(name: String, icon: ItemReference)

}