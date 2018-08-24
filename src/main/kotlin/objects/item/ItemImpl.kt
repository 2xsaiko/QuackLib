package therealfarfetchd.quacklib.objects.item

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItem
import therealfarfetchd.quacklib.api.objects.world.WorldMutable

class ItemImpl(
  override val type: ItemType,
  val stack: ItemStack
) : Item {

  constructor(type: ItemType, amount: Int, meta: Int) : this(type, ItemStack(unsafe { type.toMCItemType() }, amount, meta))

  constructor(type: ItemType) : this(type, 1, 0)

  constructor(item: MCItem) : this(ItemTypeImpl.getItem(item.item), item)

  override var count: Int
    get() = stack.count
    set(value) {
      stack.count = value
    }

  override fun spawnAt(world: WorldMutable, pos: Vec3) {
    unsafe {
      val w = world.toMCWorld()
      val e = EntityItem(w, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack.copy())
      w.spawnEntity(e)
    }
  }

  override fun spawnAt(world: WorldMutable, pos: Vec3, speed: Vec3) {
    unsafe {
      val w = world.toMCWorld()
      val e = EntityItem(w, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), stack.copy())
      e.setVelocity(speed.x.toDouble(), speed.y.toDouble(), speed.z.toDouble())
      w.spawnEntity(e)
    }
  }

  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    stack.getCapability(capability, facing)

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    stack.hasCapability(capability, facing)

  override val behavior = type.behavior

  override fun copy(): Item =
    ItemImpl(type, stack.copy())

  override fun Unsafe.toMCItem(): MCItem = stack

  override fun toString(): String {
    return "${count}×${type.registryName}:${stack.metadata} ($type)"
  }

}