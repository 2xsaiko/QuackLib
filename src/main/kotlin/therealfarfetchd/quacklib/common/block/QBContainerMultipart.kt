package therealfarfetchd.quacklib.common.block

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.slot.IPartSlot
import mcmultipart.util.MCMPWorldWrapper
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.extensions.plus

/**
 * Created by marco on 09.07.17.
 */
open class QBContainerMultipart(rl: ResourceLocation, internal val mpfactory: () -> QBlockMultipart) : QBContainer(rl, mpfactory), IMultipart {

  override fun checkValid(world: IBlockAccess, pos: BlockPos) = world.getBlockState(pos).block == this && world.getTileEntity(pos) is QBContainerTile

  override fun requireValid(world: IBlockAccess, pos: BlockPos) {
    val block = world.getBlockState(pos).block
    check(block == this, { "Block at $pos is not $this, but $block!" })
    check(world.getTileEntity(pos) != null, { "There is no tile entity at $pos!" })
    check(world.getTileEntity(pos) is QBContainerTileMultipart, { "Tile entity at $pos is not a QBContainerTileMultipart!" })
  }

  @Suppress("USELESS_ELVIS")
  override fun tempQB(world: World?, pos: BlockPos?): QBlockMultipart = ((mpfactory ?: tempFactory)() as QBlockMultipart).also { qb -> world?.also { qb.world = it }; pos?.also { qb.pos = it } }

  override fun getQBlockAt(world: IBlockAccess, pos: BlockPos): QBlockMultipart {
    requireValid(world, pos)
    val te = world.getTileEntity(pos) as QBContainerTileMultipart
    return te.qbmp
  }

  override fun getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState?): IPartSlot {
    return getQBlockAt(world, pos).getPartSlot()
  }

  override fun getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot {
    // QBContainer.sidePlaced = facing?.opposite ?: EnumFacing.DOWN
    return tempQB(world, pos).getPlacementSlot(facing, hitX, hitY, hitZ, placer)
  }

  override fun getSelectedBoundingBox(state: IBlockState?, world: World, pos: BlockPos): AxisAlignedBB = (if (checkValid(world, pos)) getQBlockAt(world, pos).selectionBox else renderBB) + pos

  override fun getBoundingBox(part: IPartInfo): AxisAlignedBB? = (part.tile as QBContainerTile).qb.rayCollisionBox

  override fun getCollisionBoundingBox(world: World, pos: BlockPos, state: IBlockState?): AxisAlignedBB? {
    if (world is MCMPWorldWrapper) {
      val qb = (world.partInfo.tile as QBContainerTile).qb
      renderBB = qb.selectionBox
      return qb.rayCollisionBox
    } else {
      return getCollisionBoundingBox(state, world, pos)
    }
  }

  override fun getLightOpacity(state: IBlockState?): Int = this.lightOpacity
  override fun getLightValue(state: IBlockState?): Int = this.lightValue

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = mpfactory()
    if (qb is ITickable) return QBContainerTileMultipart.Ticking(qb)
    else return QBContainerTileMultipart(qb)
  }

  companion object {
    internal var renderBB: AxisAlignedBB = QBlock.FullAABB
  }
}