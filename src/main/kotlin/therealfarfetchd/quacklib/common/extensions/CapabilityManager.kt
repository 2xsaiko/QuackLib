package therealfarfetchd.quacklib.common.extensions

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import kotlin.reflect.KClass

fun <T : Any> CapabilityManager.register(kClass: KClass<T>) {
  register(kClass.java, object : Capability.IStorage<T> {
    override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) {}
    override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? = null
  }, { null })
}