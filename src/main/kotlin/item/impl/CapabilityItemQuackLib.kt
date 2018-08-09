package therealfarfetchd.quacklib.item.impl

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.tools.RegisterCapability

@RegisterCapability
class CapabilityItemQuackLib(val item: Item) {

  companion object {
    @CapabilityInject(CapabilityItemQuackLib::class)
    lateinit var CAPABILITY: Capability<CapabilityItemQuackLib>
      private set
  }

}