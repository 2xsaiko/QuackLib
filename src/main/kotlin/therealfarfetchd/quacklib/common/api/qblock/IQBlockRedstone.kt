package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.util.EnumFacing

interface IQBlockRedstone {
  private val qb: QBlock
    get() = this as QBlock

  fun canConnect(side: EnumFacing): Boolean

  fun getOutput(side: EnumFacing, strong: Boolean): Int = 0

  fun isBlockPowered(): Boolean {
    return EnumFacing.VALUES
      .filter(this::canConnect)
      .any { qb.world.getRedstonePower(qb.pos.offset(it), it) > 0 }
  }
}