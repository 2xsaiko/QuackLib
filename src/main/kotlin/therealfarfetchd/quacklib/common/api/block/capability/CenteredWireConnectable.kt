package therealfarfetchd.quacklib.common.api.block.capability

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.wires.BlockWireCentered

class CenteredWireConnectable<out T>(val wire: BlockWireCentered<T>, val side: EnumFacing) : IConnectable {
  override fun getEdge(facing: EnumFacing?): T? = if (facing == null) wire.data else null

  override fun getType(facing: EnumFacing?): ResourceLocation? = if (facing == null) wire.dataType else null

  override fun getAdditionalData(facing: EnumFacing?, key: String): Any? {
    return wire.getAdditionalData(side, facing, key)
  }
}