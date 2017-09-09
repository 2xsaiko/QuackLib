package therealfarfetchd.quacklib.common.api.block.capability

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

open class MultipartConnectable(private val components: Set<IConnectable>) : IConnectable {
  override fun getEdge(facing: EnumFacing?): Any? =
    components.mapNotNull { it.getEdge(facing) }.firstOrNull()

  override fun getType(facing: EnumFacing?): ResourceLocation? =
    components.mapNotNull { it.getType(facing) }.firstOrNull()

  override fun getAdditionalData(facing: EnumFacing?, key: String): Any? =
    components.mapNotNull { it.getAdditionalData(facing, key) }.firstOrNull()

  override fun allowCornerConnections(facing: EnumFacing?): Boolean =
    components.any { it.allowCornerConnections(facing) }
}