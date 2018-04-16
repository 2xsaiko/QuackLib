package therealfarfetchd.quacklib.common.api.block.capability

import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import therealfarfetchd.quacklib.common.api.wires.TileConnectable as CapTileConnectable

object Capabilities {
  @JvmStatic
  lateinit var Connectable: Capability<IConnectable>
    @CapabilityInject(IConnectable::class)
    @JvmSynthetic
    internal set

  @JvmStatic
  lateinit var TileConnectable: Capability<CapTileConnectable>
    @CapabilityInject(CapTileConnectable::class)
    @JvmSynthetic
    internal set
}