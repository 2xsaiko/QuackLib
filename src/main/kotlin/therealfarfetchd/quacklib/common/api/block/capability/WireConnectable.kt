package therealfarfetchd.quacklib.common.api.block.capability

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.wires.BlockWire

class WireConnectable<out T>(val wire: BlockWire<T>) : IConnectable {
  override fun getEdge(facing: EnumFacing?): T? = if (facing == wire.facing) wire.data else null

  override fun getType(facing: EnumFacing?): ResourceLocation? = if (facing == wire.facing) wire.dataType else null

  override fun allowCornerConnections(facing: EnumFacing?): Boolean = facing == wire.facing
}