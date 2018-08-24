package therealfarfetchd.quacklib.api.objects.item

import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.ICapabilityProvider
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.core.modinterface.item
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.objects.ComponentHost
import therealfarfetchd.quacklib.api.objects.Instance
import therealfarfetchd.quacklib.api.objects.Instantiable
import therealfarfetchd.quacklib.api.objects.Registered
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.tools.PositionGrid

typealias MCItemType = net.minecraft.item.Item
typealias MCItem = net.minecraft.item.ItemStack

interface ItemType : Instantiable, Registered, ComponentHost<ItemComponent> {

  fun create(amount: Int = 1, meta: Int = 0): Item

  val model: Model

  val behavior: ItemBehavior

  fun Unsafe.toMCItemType(): MCItemType

  companion object {
    val Empty: ItemType = airItemType
  }

}

interface Item : Instance<ItemType>, BehaviorDelegate, ICapabilityProvider {

  fun Unsafe.toMCItem(): MCItem

  var count: Int

  fun spawnAt(world: WorldMutable, pos: Vec3)

  fun spawnAt(world: WorldMutable, pos: Vec3, speed: Vec3)

  fun spawnAt(world: WorldMutable, pos: PositionGrid) =
    spawnAt(world, pos.toVec3() + Vec3(0.5f, 0.5f, 0.5f))

  fun spawnAt(world: WorldMutable, pos: PositionGrid, speed: Vec3) =
    spawnAt(world, pos.toVec3() + Vec3(0.5f, 0.5f, 0.5f), speed)

  @Deprecated("Internal usage", ReplaceWith("this"), DeprecationLevel.ERROR)
  override val item: Item
    get() = this

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