package therealfarfetchd.quacklib.api.block.data

import net.minecraft.nbt.NBTTagCompound
import kotlin.reflect.KClass

interface DataPartSerializationRegistry {

  fun <T : Any> register(
    type: KClass<T>,
    save: NBTTagCompound.(name: String, type: T) -> Unit,
    load: NBTTagCompound.(name: String) -> T?,
    priority: Int = 0)

  fun <T : S, S : Any> registerRedirect(type: KClass<T>, from: KClass<S>, cast: (S) -> T, priority: Int = 0)

  fun <T : Any, S : Any> registerRedirect(type: KClass<T>, from: KClass<S>, castIn: (T) -> S, castOut: (S) -> T, priority: Int = 0)

  fun <T : Any> save(tag: NBTTagCompound, name: String, value: T?)

  fun <T : Any> load(tag: NBTTagCompound, type: KClass<T>, name: String): Value<T?>?

}

data class Value<T>(val value: T)

inline fun <reified T : Any> DataPartSerializationRegistry.register(
  noinline save: NBTTagCompound.(name: String, obj: T?) -> Unit,
  noinline load: NBTTagCompound.(name: String) -> T?) =
  register(T::class, save, load)
