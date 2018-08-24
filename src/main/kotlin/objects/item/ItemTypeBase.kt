package therealfarfetchd.quacklib.objects.item

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.objects.item.ItemType

abstract class ItemTypeBase(override val registryName: ResourceLocation) : ItemType {

  override fun equals(other: Any?): Boolean =
    other is ItemType && other.registryName == registryName

  override fun hashCode(): Int =
    registryName.hashCode()

  override fun toString(): String {
    return "Item '$registryName'"
  }

}