package therealfarfetchd.quacklib.render.property

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponentRenderProperties
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponentRenderProperties
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.render.property.PropertyType
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyBlock
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation
import therealfarfetchd.quacklib.block.data.render.PropertyData
import therealfarfetchd.quacklib.block.data.render.PropertyDataExtended
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

class RenderPropertyBlockImpl<C : BlockComponentRenderProperties, T>(
  val targetClass: KClass<out C>,
  val rl: ResourceLocation,
  override val name: String,
  val type: KClass<*>,
  val outputOp: (Block) -> T,
  constraints: (T) -> Boolean,
  values: List<T>?
) : RenderPropertyBlock<C, T> {

  val useExtendedProperty: Boolean =
    if (type.isSubclassOf(Enum::class)) false
    else values == null

  val pt: PropertyType<T> =
    if (useExtendedProperty) {
      val cs =
        if (values != null) { t: T -> t in values && constraints(t) }
        else { t: T -> constraints(t) }

      PropertyType.Extended(PropertyDataExtended(PropertyResourceLocation(rl, name), type, cs))
    } else {
      @Suppress("UNCHECKED_CAST")
      val v = values ?: type.javaObjectType.enumConstants.toList() as List<T>

      PropertyType.Standard(PropertyData(PropertyResourceLocation(rl, name), type, v.filter(constraints)))
    }

  override fun getValue(container: Block): T {
    return outputOp(container)
  }

  override fun getComponentClass(): KClass<out C> {
    return targetClass
  }

  override fun Unsafe.getMCProperty(): PropertyType<T> = pt

}

class RenderPropertyItemImpl<C : ItemComponentRenderProperties, T>(
  val targetClass: KClass<out C>,
  override val name: String,
  val outputOp: (Item) -> T,
  constraints: (T) -> Boolean,
  values: List<T>?
) : RenderProperty<C, Item, T> {

  override fun getValue(container: Item): T {
    return outputOp(container)
  }

  override fun getComponentClass(): KClass<out C> {
    return targetClass
  }

}