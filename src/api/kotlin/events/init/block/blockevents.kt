package therealfarfetchd.quacklib.api.events.init.block

import net.minecraftforge.fml.common.eventhandler.Event
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope

class EventBlockCreation(val cs: BlockConfigurationScope) : Event()

class EventAttachComponent(val cs: BlockConfigurationScope, val c: BlockComponent) : Event()