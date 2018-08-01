package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.render.model.Model

@InitDSL
interface ItemConfigurationScope : ItemConfiguration {

  fun apply(component: ItemComponent)

  fun <T : Model> apply(renderer: T): T

}