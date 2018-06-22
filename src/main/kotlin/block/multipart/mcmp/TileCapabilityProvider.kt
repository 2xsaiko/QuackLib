package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.ref.MCMPCapabilities
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class TileCapabilityProvider(val self: TileQuackLib) : ICapabilityProvider {

  val partTile = TileMultipartQuackLib(self)

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    when (capability) {
      MCMPCapabilities.MULTIPART_TILE -> partTile as T
      else -> null
    }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    when (capability) {
      MCMPCapabilities.MULTIPART_TILE -> true
      else -> false
    }

}