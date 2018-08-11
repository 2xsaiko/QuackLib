package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.render.model.Model

@InitDSL
interface ItemConfigurationScope : ItemConfiguration {

  /**
   *
   */
  fun <T : ItemComponent> apply(component: T): T

  /**
   *
   */
  fun <T : Model> useModel(model: T): T

  /**
   *
   */
  fun link(op: ItemLinkScope.() -> Unit)

}