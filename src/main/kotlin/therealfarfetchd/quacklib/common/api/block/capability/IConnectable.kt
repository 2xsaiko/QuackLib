package therealfarfetchd.quacklib.common.api.block.capability

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation

/**
 * Generic cable connection peripheral.
 */
interface IConnectable {
  /**
   * Returns the connectable on the specified edge (center of the face if null).
   */
  fun getEdge(facing: EnumFacing?): Any?

  /**
   * Returns the type of the connectable on the specified edge (e.g. retrocomputers:bus for RC's bus)
   */
  fun getType(facing: EnumFacing?): ResourceLocation?

  fun allowCornerConnections(facing: EnumFacing?): Boolean = false
}