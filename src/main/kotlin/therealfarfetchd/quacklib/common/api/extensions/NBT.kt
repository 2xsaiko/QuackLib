package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagLongArray
import net.minecraftforge.common.util.INBTSerializable
import therealfarfetchd.quacklib.common.api.util.QNBTCompound
import java.lang.invoke.MethodHandles

fun INBTSerializable<NBTTagCompound>.saveData(): QNBTCompound {
  return QNBTCompound(serializeNBT())
}

fun INBTSerializable<NBTTagCompound>.loadData(nbt: QNBTCompound) {
  deserializeNBT(nbt.self)
}

val longArrayPtr by lazy {
  val field = NBTTagLongArray::class.java.declaredFields[0]
  field.isAccessible = true
  MethodHandles.lookup().unreflectGetter(field)
}

val NBTTagLongArray.longArray: LongArray
  get() = longArrayPtr.invokeKt(this)