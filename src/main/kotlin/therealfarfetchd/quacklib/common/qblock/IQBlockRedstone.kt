package therealfarfetchd.quacklib.common.qblock

import net.minecraft.util.EnumFacing

interface IQBlockRedstone {

  fun canConnect(side: EnumFacing): Boolean

  fun getOutput(side: EnumFacing, strong: Boolean): Int

  fun updateInput(side: EnumFacing, str: Int, strong: Boolean)

}