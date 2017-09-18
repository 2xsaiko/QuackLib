package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

fun INBTSerializable<NBTTagCompound>.saveData(): QNBTCompound {
  return QNBTCompound(serializeNBT())
}

fun INBTSerializable<NBTTagCompound>.loadData(nbt: QNBTCompound) {
  deserializeNBT(nbt.self)
}