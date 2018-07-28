package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.render.model.ItemModel

@InitDSL
interface ItemConfigurationScope : ItemConfiguration {

  fun apply(component: ItemComponent)

  fun <T : ItemModel> apply(renderer: T): T

}