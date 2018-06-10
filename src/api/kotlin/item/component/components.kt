package therealfarfetchd.quacklib.api.item.component

import therealfarfetchd.quacklib.api.core.init.Applyable
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope

private typealias Base = ItemComponent

interface ItemComponent : Applyable<ItemConfigurationScope>

interface ItemComponentTool : Base {

  val toolTypes: Set<Tool>

}

interface ItemComponentUse : Base {

  fun onUse()

}