package therealfarfetchd.quacklib.objects.item

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.item.init.ItemConfiguration
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItemType
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.item.impl.ItemQuackLib
import therealfarfetchd.quacklib.render.client.model.ModelError

class ItemTypeImpl(val conf: ItemConfiguration) : ItemTypeBase(conf.rl) {

  lateinit var item: MCItemType

  override val components: List<ItemComponent> = conf.components

  override val model: Model = conf.model

  override val behavior = StandardItemBehavior(this)

  override fun create(amount: Int, meta: Int): Item =
    ItemImpl(this, amount, meta)

  override fun Unsafe.toMCItemType(): MCItemType = item

  override fun toString(): String {
    return "Item '$registryName' (${components.size} components)"
  }

  companion object {

    val map = mutableMapOf<ResourceLocation, ItemType>()

    fun getItem(mc: MCItemType): ItemType {
      if (mc is ItemQuackLib) return mc.type

      val rl = mc.registryName!!

      return map.getOrPut(rl) { ItemTypeImpl.Vanilla(mc) }
    }

    fun getItem(rl: ResourceLocation): ItemType? {
      map[rl]?.also { return it }

      val item = with(ForgeRegistries.ITEMS) { getValue(rl).takeIf { containsKey(rl) } }
                 ?: return null

      val bt = (item as? ItemQuackLib)?.type ?: ItemTypeImpl.Vanilla(item)
      map[rl] = bt
      return bt
    }

    fun addItem(type: ItemTypeImpl, mc: MCItemType) {
      type.item = mc
      map[type.registryName] = type
    }

  }

  class Vanilla(val item: MCItemType) : ItemTypeBase(item.registryName!!) {

    override val components: List<ItemComponent>
      get() = emptyList()

    override val model: Model
      get() = ModelError

    override val behavior = VanillaItemBehavior(item)

    override fun create(amount: Int, meta: Int): Item =
      ItemImpl(this, amount, meta)

    override fun Unsafe.toMCItemType(): MCItemType = item

  }

}