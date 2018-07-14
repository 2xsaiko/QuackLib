package therealfarfetchd.quacklib.api.block.capabilities

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import therealfarfetchd.quacklib.api.block.redstone.ConnectionMask
import therealfarfetchd.quacklib.api.tools.RegisterCapability

@RegisterCapability
interface CapabilityMulti {

  fun hasCapability(cap: Capability<*>, edge: ConnectionMask): Boolean

  fun <T> getCapability(cap: Capability<T>, edge: ConnectionMask): T?

  companion object {
    @CapabilityInject(CapabilityMulti::class)
    lateinit var CAPABILITY: Capability<CapabilityMulti>
      private set
  }

}