package therealfarfetchd.quacklib.common.extensions

import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.qblock.QBlock

/**
 * Created by marco on 09.07.17.
 */

val World.isClient
  get() = this.isRemote

val World.isServer
  get() = !this.isRemote

fun IBlockAccess.getQBlock(pos: BlockPos): QBlock? = (getTileEntity(pos) as? QBContainerTile)?.qb

fun IBlockAccess.getQBlock(pos: BlockPos, slot: IPartSlot): QBlock? {
  val te = getTileEntity(pos)
  if (te is QBContainerTile) {
    val qb = te.qb
    if (qb is IQBlockMultipart && qb.getPartSlot() == slot) return qb
  }
  if (te is TileMultipartContainer) {
    return (te.getPartTile(slot).orElse(null)?.tileEntity as? QBContainerTile)?.qb
  }
  return null
}

fun TileEntity.notifyNeighborsOfSides(vararg sides: EnumFacing) {
  for (side in sides) {
    val p = pos.offset(side)
    world.notifyNeighborsOfStateExcept(p, world.getBlockState(p).block, side.opposite)
  }
}