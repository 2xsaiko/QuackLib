package therealfarfetchd.quacklib.item.impl

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import therealfarfetchd.quacklib.api.item.component.ItemComponentTool
import therealfarfetchd.quacklib.api.item.init.ItemConfiguration

class ItemQuackLib(def: ItemConfiguration) : Item() {

  val components = def.components

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()

    getComponentsOfType<ItemComponentTool>()
      .flatMap(ItemComponentTool::toolTypes)
      .forEach { setHarvestLevel(it.toolName, it.level) }
  }

  override fun isInCreativeTab(targetTab: CreativeTabs): Boolean {
    return targetTab is TabQuackLib || targetTab == CreativeTabs.SEARCH
  }

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}