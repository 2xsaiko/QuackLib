@file:Suppress("UNCHECKED_CAST")

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
    val persistent: Boolean
    val sync: Boolean
    val validValues: List<T>?
    val serializer: Serializer<T>

    fun isValid(value: T): Boolean

  }

}

interface Serializer<T> {

  fun load(name: String, nbt: NBTTagCompound): Result<T>

  fun save(name: String, nbt: NBTTagCompound, t: T)

  data class Result<T>(val value: T, val isDefault: Boolean)

}

abstract class SimpleSerializer<T> : Serializer<T> {

  override fun load(name: String, nbt: NBTTagCompound): Serializer.Result<T> =
    Serializer.Result(load(nbt.getCompoundTag(name)), !nbt.hasKey(name))

  override fun save(name: String, nbt: NBTTagCompound, t: T) {
    val tag = NBTTagCompound()
    save(tag, t)
    nbt.setTag(name, tag)
  }

  abstract fun load(nbt: NBTTagCompound): T

  abstract fun save(nbt: NBTTagCompound, t: T)

}

class DefaultSerializer<T>(val type: KClass<*>, val default: () -> T) : Serializer<T> {

  override fun load(name: String, nbt: NBTTagCompound): Serializer.Result<T> {
    val r = QuackLibAPI.impl.serializationRegistry.load(nbt, type, name)
    return if (r == null) Serializer.Result(default(), true)
    else Serializer.Result(r.value as T, false)
  }

  override fun save(name: String, nbt: NBTTagCompound, t: T) {
    QuackLibAPI.impl.serializationRegistry.save<Any>(nbt, name, t)
  }

}

// FIXME this is type inference hell

fun <T> BlockDataPart.data(name: String, type: KClass<*>, serializer: Serializer<T>, persistent: Boolean, sync: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T> =
  QuackLibAPI.impl.createBlockDataDelegate(this, name, type, serializer, persistent, sync, validValues)

inline fun <reified T> BlockDataPart.data(name: String, serializer: Serializer<T>, persistent: Boolean = true, sync: Boolean = false, validValues: Iterable<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, serializer, persistent, sync, validValues?.toList())

inline fun <reified T> BlockDataPart.data(name: String, serializer: Serializer<T>, persistent: Boolean = true, sync: Boolean = false, validValues: Array<T>): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, serializer, persistent, sync, validValues.toList())

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, validValues: Iterable<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class) { default }, persistent, sync, validValues?.toList())

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, validValues: Array<T>): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class) { default }, persistent, sync, validValues.toList())

inline fun <reified T : Enum<T>> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class) { default }, persistent, sync, T::class.java.enumConstants.toList())

inline fun <reified T> BlockDataPart.data(name: String, noinline default: () -> T, persistent: Boolean = true, sync: Boolean = false, validValues: Iterable<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class, default), persistent, sync, validValues?.toList())

inline fun <reified T> BlockDataPart.data(name: String, noinline default: () -> T, persistent: Boolean = true, sync: Boolean = false, validValues: Array<T>): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class, default), persistent, sync, validValues.toList())

inline fun <reified T : Enum<T>> BlockDataPart.data(name: String, noinline default: () -> T, persistent: Boolean = true, sync: Boolean = false): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, DefaultSerializer(T::class, default), persistent, sync, T::class.java.enumConstants.toList())