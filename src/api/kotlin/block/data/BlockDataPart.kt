package therealfarfetchd.quacklib.api.block.data

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

    fun isValid(value: T): Boolean

  }

}

fun <T> BlockDataPart.data(name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T> =
  QuackLibAPI.impl.createBlockDataDelegate(this, name, type, default, persistent, sync, validValues)

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, validValues: Iterable<T>? = null): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, validValues?.toList())

inline fun <reified T> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false, validValues: Array<T>): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, validValues.toList())

inline fun <reified T : Enum<T>> BlockDataPart.data(name: String, default: T, persistent: Boolean = true, sync: Boolean = false): ReadWriteProperty<BlockDataPart, T> =
  data(name, T::class, default, persistent, sync, T::class.java.enumConstants.toList())