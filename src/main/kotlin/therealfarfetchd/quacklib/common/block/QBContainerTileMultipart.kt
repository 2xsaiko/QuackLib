package therealfarfetchd.quacklib.common.block

import mcmultipart.api.multipart.IMultipartTile
import mcmultipart.api.ref.MCMPCapabilities
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability

/**
 * Created by marco on 09.07.17.
 */
open class QBContainerTileMultipart() : QBContainerTile(), IMultipartTile {

  val qbmp: QBlockMultipart
    get() = qb as QBlockMultipart

  constructor(qbIn: QBlockMultipart) : this() {
    qb = qbIn
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
    return capability == MCMPCapabilities.MULTIPART_TILE || super.hasCapability(capability, facing)
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
    return when (capability) {
      MCMPCapabilities.MULTIPART_TILE -> this as T
      else -> super.getCapability(capability, facing)
    }
  }

  open class Ticking() : QBContainerTileMultipart(), ITickingQBTile {
    constructor(qbIn: QBlockMultipart) : this() {
      QBContainerTileMultipart(qbIn)
    }
  }

}