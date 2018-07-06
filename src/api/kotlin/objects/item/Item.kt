package therealfarfetchd.quacklib.api.objects.item

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.objects.ComponentHost
import therealfarfetchd.quacklib.api.objects.Instance
import therealfarfetchd.quacklib.api.objects.Instantiable
import therealfarfetchd.quacklib.api.objects.Registered

typealias MCItemType = net.minecraft.item.Item
typealias MCItem = net.minecraft.item.ItemStack

interface ItemType : Instantiable, Registered, ComponentHost<ItemComponent> {

  fun Unsafe.toMCItemType(): MCItemType

  fun create(amount: Int = 1, meta: Int = 0): Item

}

interface Item : Instance<ItemType> {

  fun Unsafe.toMCItem(): MCItem

  var count: Int

  fun copy(): Item

}

private val airItemType by lazy { item(ResourceLocation("minecraft", "air")) }

fun MCItem.toItem(): Item = QuackLibAPI.impl.convertItem(this)

fun MCItemType.toItemType(): ItemType = item(registryName!!)

fun ItemType?.orEmpty(): ItemType = this ?: airItemType

fun Item?.orEmpty(): Item = this ?: airItemType.create()

interface UnsafeExtItem : Unsafe {

  fun ItemType.toMCItemType() =
    self.toMCItemType()

  fun Item.toMCItem() =
    self.toMCItem()

}