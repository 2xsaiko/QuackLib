package therealfarfetchd.quacklib.api.events.init.item

import net.minecraftforge.fml.common.eventhandler.Event
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope

class EventItemCreation(val cs: ItemConfigurationScope) : Event()

class EventAttachComponent(val cs: ItemConfigurationScope, val c: ItemComponent) : Event()