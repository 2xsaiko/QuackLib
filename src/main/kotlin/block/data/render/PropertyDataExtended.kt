package therealfarfetchd.quacklib.block.data.render

import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation

class PropertyDataExtended<T>(propName: PropertyResourceLocation, val def: BlockDataPart.ValueProperties<T>) : IUnlistedProperty<T> {

  private val n = propName.toString()

  override fun valueToString(value: T): String = value.toString()

  override fun getName(): String = n

  @Suppress("UNCHECKED_CAST")
  override fun getType(): Class<T> = def.type.javaObjectType as Class<T>

  override fun isValid(value: T): Boolean {
    return def.isValid(value)
  }

}