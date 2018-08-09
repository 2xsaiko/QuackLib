package therealfarfetchd.quacklib.item.impl

import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import therealfarfetchd.quacklib.api.item.component.ItemComponentCapability
import therealfarfetchd.quacklib.api.objects.getComponentsOfType
import therealfarfetchd.quacklib.api.objects.item.Item

class CapabilityProviderItem(val item: Item) : ICapabilityProvider {

  val storage = CapabilityItemQuackLib(item)

  val cItem = item.type.getComponentsOfType<ItemComponentCapability>()

  @Suppress("UNCHECKED_CAST")
  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? = when (capability) {
    CapabilityItemQuackLib.CAPABILITY -> storage as T
    else -> cItem.firstOrNull { it.hasCapability(item, capability, facing) }?.getCapability(item, capability, facing)
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean = when (capability) {
    CapabilityItemQuackLib.CAPABILITY -> true
    else -> cItem.any { it.hasCapability(item, capability, facing) }
  }

}