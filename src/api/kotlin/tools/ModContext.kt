package therealfarfetchd.quacklib.api.tools

import net.minecraftforge.fml.common.ModContainer

interface ModContext {

  fun <R> lockMod(op: () -> R): R

  fun currentMod(): ModContainer?

}