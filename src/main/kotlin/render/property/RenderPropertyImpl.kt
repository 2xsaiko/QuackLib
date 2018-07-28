package therealfarfetchd.quacklib.render.property

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.render.property.PropertyType
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation
import therealfarfetchd.quacklib.block.data.render.PropertyData
import therealfarfetchd.quacklib.block.data.render.PropertyDataExtended
import kotlin.reflect.KClass

class RenderPropertyImpl<T>(
  val rl: ResourceLocation,
  override val name: String,
  val type: KClass<*>,
  val outputOp: (Block) -> T,
  constraints: (T) -> Boolean,
  values: List<T>?
) : RenderProperty<T> {

  val useExtendedProperty = true // TODO

  val pt: PropertyType<T> =
    if (useExtendedProperty) {
      val cs =
        if (values != null) { t: T -> t in values && constraints(t) }
        else { t: T -> constraints(t) }

      PropertyType.Extended(PropertyDataExtended(PropertyResourceLocation(rl, name), type, cs))
    } else {
      PropertyType.Standard(PropertyData(PropertyResourceLocation(rl, name), type, values!!.filter(constraints)))
    }

  override fun getValue(b: Block): T {
    return outputOp(b)
  }

  override fun Unsafe.getMCProperty(): PropertyType<T> = pt

}