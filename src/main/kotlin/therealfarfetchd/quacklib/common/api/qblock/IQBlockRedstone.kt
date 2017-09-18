package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.util.EnumFacing

interface IQBlockRedstone {
  fun canConnect(side: EnumFacing): Boolean

  fun getOutput(side: EnumFacing, strong: Boolean): Int = 0

  fun updateInput(side: EnumFacing, str: Int, strong: Boolean) {}
}