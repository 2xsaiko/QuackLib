package therealfarfetchd.quacklib.common.extensions

import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
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