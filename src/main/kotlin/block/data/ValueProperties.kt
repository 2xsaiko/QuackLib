package therealfarfetchd.quacklib.block.data

import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import kotlin.reflect.KClass

data class ValuePropertiesImpl<T>(
  override val name: String,
  override val type: KClass<*>,
  override val default: T,
  override val persistent: Boolean,
  override val sync: Boolean,
  override val validValues: List<T>?
) : BlockDataPart.ValueProperties<T> {

  override fun isValid(value: T): Boolean =
    if (validValues == null) true
    else value in validValues

}