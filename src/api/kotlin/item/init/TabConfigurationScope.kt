package therealfarfetchd.quacklib.api.item.init

import therealfarfetchd.quacklib.api.core.init.InitDSL
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.objects.item.ItemType

@InitDSL
interface TabConfigurationScope : TabConfiguration {

  fun include(item: ItemType)

  fun include(item: String) =
    include(item(item))

}