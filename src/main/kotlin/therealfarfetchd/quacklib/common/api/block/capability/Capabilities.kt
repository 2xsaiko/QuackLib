package therealfarfetchd.quacklib.common.api.block.capability

import mcmultipart.capability.CapabilityJoiner
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import therealfarfetchd.quacklib.common.api.autoconf.DefaultFeatures
import therealfarfetchd.quacklib.common.api.autoconf.FeatureManager

object Capabilities {

  private var _Connectable: Capability<IConnectable>? = null

  @JvmStatic
  var Connectable: Capability<IConnectable>
    get() = _Connectable!!
    @CapabilityInject(IConnectable::class)
    set(value) {
      if (_Connectable != null) throw IllegalStateException("Capability already initialized!")
      _Connectable = value
      if (FeatureManager.dependSoft(DefaultFeatures.MultipartMod))
        CapabilityJoiner.registerCapabilityJoiner(value) { MultipartConnectable(it.toSet()) }
    }
}