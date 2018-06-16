package therealfarfetchd.quacklib.block.data.render

import com.google.common.base.Optional
import net.minecraft.block.properties.IProperty
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation
import kotlin.reflect.full.isSubclassOf

class PropertyData<T>(propName: PropertyResourceLocation, val def: BlockDataPart.ValueProperties<T>) : IProperty<PropertyData.Wrapper<T>> {

  val n: String = propName.property
    .replace(Regex("([a-z])([A-Z])"), "$1_$2").toLowerCase()
    .replace(Regex("[^a-z0-9_]"), "_")

  init {
    if (def.validValues == null) error("Needs value bounds!")
  }

  val allowedTW: Map<T, Wrapper<T>> = def.validValues!!.associate { it to WrapperImpl(it) }

  fun wrap(value: T): Wrapper<T>? = allowedTW[value]

  override fun parseValue(value: String?): Optional<Wrapper<T>> = Optional.absent()

  @Suppress("UNCHECKED_CAST")
  override fun getValueClass(): Class<Wrapper<T>> = Wrapper::class.java as Class<Wrapper<T>>

  override fun getName(): String = n

  override fun getName(value: Wrapper<T>): String = value.value.toString()

  override fun getAllowedValues(): Collection<Wrapper<T>> = allowedTW.values

  interface Wrapper<T> : Comparable<Wrapper<T>> {
    val value: T
  }

  private inner class WrapperImpl(override val value: T) : Wrapper<T> {

    @Suppress("UNCHECKED_CAST")
    override fun compareTo(other: Wrapper<T>): Int {
      return if (def.type.isSubclassOf(Comparable::class)) {
        value as Comparable<T>
        other.value as Comparable<T>
        value.compareTo(other.value)
      } else {
        allowedTW.values.indexOf(this) - allowedTW.values.indexOf(other)
      }
    }

    override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (other !is Wrapper<*>) return false

      if (value != other.value) return false

      return true
    }

    override fun hashCode(): Int {
      return value?.hashCode() ?: 0
    }

    override fun toString(): String {
      return value.toString()
    }

  }

}