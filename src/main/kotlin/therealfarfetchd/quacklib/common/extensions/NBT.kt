package therealfarfetchd.quacklib.common.extensions

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import therealfarfetchd.quacklib.common.util.QNBTCompound

fun INBTSerializable<NBTTagCompound>.saveData(): QNBTCompound {
  return QNBTCompound(serializeNBT())
}

fun INBTSerializable<NBTTagCompound>.loadData(nbt: QNBTCompound) {
  deserializeNBT(nbt.self)
}