package therealfarfetchd.quacklib.api.block.capabilities

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import therealfarfetchd.quacklib.api.tools.RegisterCapability

@RegisterCapability
interface CapabilityRedstone {

  companion object {
    @CapabilityInject(CapabilityRedstone::class)
    lateinit var CAPABILITY: Capability<CapabilityRedstone>
      private set
  }

}