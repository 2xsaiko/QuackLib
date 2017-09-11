package therealfarfetchd.quacklib.common.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.QCreativeTab
import therealfarfetchd.quacklib.common.extensions.makeStack
import therealfarfetchd.quacklib.common.feature.FeatureManager
import therealfarfetchd.quacklib.common.feature.ItemFeature

object ItemComponent : Item() {
  init {
    registryName = ResourceLocation(ModID, "component")
    unlocalizedName = registryName.toString()
    hasSubtypes = true
    maxDamage = 0
    creativeTab = QCreativeTab
  }

  override fun getUnlocalizedName(stack: ItemStack?): String = "$unlocalizedName.${stack?.metadata}"

  override fun getSubItems(tab: CreativeTabs?, items: NonNullList<ItemStack>) {
    if (this.isInCreativeTab(tab)) {
      getValidMetadata().forEach { items.add(makeStack(meta = it)) }
    }
  }

  fun getValidMetadata(): List<Int> = FeatureManager.enabled.keys.mapNotNull { it as? ItemFeature }.map { it.meta }.toSet().sorted()
}