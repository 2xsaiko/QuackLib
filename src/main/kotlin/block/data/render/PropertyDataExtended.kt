package therealfarfetchd.quacklib.block.data.render

import net.minecraft.util.IStringSerializable
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation

class PropertyDataExtended<T>(propName: PropertyResourceLocation, val def: BlockDataPart.ValueProperties<T>) : IUnlistedProperty<T> {

  // TODO expand name if ambiguous
  private val n = propName.property

  override fun valueToString(value: T): String =
    if (value is IStringSerializable) value.name
    else value.toString()

  override fun getName(): String = n

  @Suppress("UNCHECKED_CAST")
  override fun getType(): Class<T> = def.type.javaObjectType as Class<T>

  override fun isValid(value: T): Boolean {
    return def.isValid(value)
  }

}