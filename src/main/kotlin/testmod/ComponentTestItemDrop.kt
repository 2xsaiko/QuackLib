package therealfarfetchd.quacklib.testmod

import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentActivation
import therealfarfetchd.quacklib.api.block.component.BlockData
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.ValidationContext

class ComponentTestItemDrop(val item: ItemStack) : BlockComponentActivation {

  override fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean {
    val (world, pos, _) = data
    if (!world.isRemote) {
      val item = EntityItem(world, pos.x.toDouble(), (pos.y + 1).toDouble(), pos.z.toDouble())
      item.item = this.item.copy()
      world.spawnEntity(item)
    }
    return true
  }

  override fun validate(target: BlockConfigurationScope, vc: ValidationContext) {
    vc.info("Hey, this is a test class my dude! Don't use this in a real mod :P")
  }

}