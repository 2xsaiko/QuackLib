package therealfarfetchd.quacklib.testmod

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.api.block.component.BlockComponentActivation

class ComponentTestItemDrop(val item: ItemStack) : BlockComponentActivation {

  override fun onActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    if (!world.isRemote) {
      val item = EntityItem(world, pos.x.toDouble(), (pos.y + 1).toDouble(), pos.z.toDouble())
      item.item = this.item.copy()
      world.spawnEntity(item)
    }
    return true
  }

}