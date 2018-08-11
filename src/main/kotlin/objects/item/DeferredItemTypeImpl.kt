package therealfarfetchd.quacklib.objects.item

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemBehavior
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItemType
import therealfarfetchd.quacklib.api.render.model.Model

class DeferredItemTypeImpl(override val registryName: ResourceLocation) : ItemType {

  init {
    instances += this
  }

  var realInstance: ItemType? = null

  override fun Unsafe.toMCItemType(): MCItemType = unsafe { realInstance?.toMCItemType() }
                                                   ?: crash()

  override fun create(amount: Int, meta: Int): Item =
    realInstance?.create(amount, meta)
    ?: crash()

  override val behavior: ItemBehavior
    get() = realInstance?.behavior
            ?: crash()

  override val components: List<ItemComponent>
    get() = realInstance?.components
            ?: crash()

  override val model: Model
    get() = realInstance?.model
            ?: crash()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun crash(): Nothing = error("Item not resolved yet! Come back after init is done")

  companion object {
    var instances: Set<DeferredItemTypeImpl> = emptySet()
    var isInit = true
  }

}