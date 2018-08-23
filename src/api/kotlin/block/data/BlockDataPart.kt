package therealfarfetchd.quacklib.api.block.data

import net.minecraft.nbt.NBTTagCompound
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass

abstract class BlockDataPart(val version: Int) {

  lateinit var storage: Storage
    @JvmName("setStorage0")
    private set

  var defs: Map<String, ValueProperties<Any?>> = emptyMap()
    private set

  fun setStorage(storage: Storage) {
    this.storage = storage
  }

  fun addDefinition(name: String, prop: ValueProperties<Any?>) {
    defs += name to prop
  }

  interface Storage

  interface ValueProperties<T> {

    val name: String
    val type: KClass<*>
    val default: T
    val persistent: Boolean
    val sync: Boolean
    val validValues: List<T>?
    val serializer: Serializer<T>?

    fun isValid(value: T): Boolean

  }

}

data class Serializer<T>(val load: (NBTTagCompound) -> Value<T>?, val save: (T, NBTTagCompound) -> Unit)

fun <T> BlockDataPart.data(name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, validValues: List<T>?, serializer: Serializer<T>?): ReadWriteProperty<BlockDataPart, T> =
  QuackLibAPI.impl.createBlockDataDelegate(this, name, type, default, persistent, sync, validValues, serializer)

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, serializer: Serializer<T>? = null, validValues: Iterable<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, validValues?.toList(), serializer)

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, serializer: Serializer<T>? = null, validValues: Array<T>): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, validValues.toList(), serializer)

inline fun <reified T : Enum<T>> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, serializer: Serializer<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, T::class.java.enumConstants.toList(), serializer)