package therealfarfetchd.quacklib.item.impl

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.item.component.ItemComponentTool
import therealfarfetchd.quacklib.api.item.component.ItemComponentUse
import therealfarfetchd.quacklib.api.item.init.ItemConfiguration

class ItemQuackLib(def: ItemConfiguration) : Item() {

  val components = def.components.asReversed()

  val cUse = getComponentsOfType<ItemComponentUse>()

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()

    getComponentsOfType<ItemComponentTool>()
      .flatMap(ItemComponentTool::toolTypes)
      .forEach { setHarvestLevel(it.toolName, it.level) }
  }

  override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
    for (component in cUse) {
      val ret = component.onUse(player.getHeldItem(hand), player, worldIn, pos, hand, facing, Vec3(hitX, hitY, hitZ))
      if (ret != EnumActionResult.PASS) return ret
    }
    return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ)
  }

  override fun isInCreativeTab(targetTab: CreativeTabs): Boolean {
    return targetTab is TabQuackLib || targetTab == CreativeTabs.SEARCH
  }

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}