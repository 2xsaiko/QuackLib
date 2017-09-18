package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IWorldNameable
import net.minecraft.world.World

class QBContainerInventory(factory: () -> QBlock) : QBContainer(factory) {
  /**
   * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
   * Block.removedByPlayer
   */
  override fun harvestBlock(worldIn: World, player: EntityPlayer, pos: BlockPos, state: IBlockState, te: TileEntity?, stack: ItemStack) {
    if (te is IWorldNameable && (te as IWorldNameable).hasCustomName()) {
      player.addStat(StatList.getBlockStats(this)!!)
      player.addExhaustion(0.005f)

      if (worldIn.isRemote) {
        return
      }

      val i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack)
      val item = this.getItemDropped(state, worldIn.rand, i)

      if (item == Items.AIR) {
        return
      }

      val itemstack = ItemStack(item, this.quantityDropped(worldIn.rand))
      itemstack.setStackDisplayName((te as IWorldNameable).name)
      Block.spawnAsEntity(worldIn, pos, itemstack)
    } else {
      super.harvestBlock(worldIn, player, pos, state, null as TileEntity?, stack)
    }
  }

  /**
   * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
   * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
   * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
   */
  override fun eventReceived(state: IBlockState, worldIn: World, pos: BlockPos, id: Int, param: Int): Boolean {
    super.eventReceived(state, worldIn, pos, id, param)
    val tileentity = worldIn.getTileEntity(pos)
    return tileentity?.receiveClientEvent(id, param) ?: false
  }
}