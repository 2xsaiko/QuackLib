package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.component.ItemComponent

@InitDSL
interface ItemConfigurationScope : ItemConfiguration {

  fun apply(component: ItemComponent)

}