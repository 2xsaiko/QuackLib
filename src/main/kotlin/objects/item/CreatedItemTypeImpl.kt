package therealfarfetchd.quacklib.objects.item

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfiguration
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItemType

class CreatedItemTypeImpl(override val registryName: ResourceLocation, val def: ItemConfiguration) : ItemType {

  var realInstance: ItemType? = null

  init {
    instances += this
  }

  override fun create(amount: Int, meta: Int): Item =
    realInstance?.create()
    ?: crash()

  override val components: List<ItemComponent>
    get() = realInstance?.components
            ?: def.components

  override fun Unsafe.toMCItemType(): MCItemType = unsafe { realInstance?.toMCItemType() }
                                                   ?: crash()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun crash(): Nothing = error("Item $registryName not resolved yet! Come back after init is done")

  companion object {
    var instances: Set<CreatedItemTypeImpl> = emptySet()
  }

}