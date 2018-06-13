package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.ItemReference

@InitDSL
interface TabConfigurationScope : TabConfiguration {

  fun include(item: ItemReference)

  fun include(item: String) =
    include(item(item))

}