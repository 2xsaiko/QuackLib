package therealfarfetchd.quacklib.common.api.qblock

import mcmultipart.MCMultiPart
import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.extensions.plus
import java.util.*

/**
 * Created by marco on 09.07.17.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
open class QBContainerMultipart(factory: () -> QBlock) : QBContainer(factory), IMultipart {
  init {
    check(factory() is IQBlockMultipart) { "Illegal type: factory() must be QBlock with IQBlockMultipart" }
  }

  override fun getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState?): IPartSlot {
    return world.getQBlock(pos)!!.asmp.getPartSlot()
  }

  override fun getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot {
    QBContainer.sidePlaced = facing?.opposite ?: EnumFacing.DOWN
    return buildPart(world, pos) {
      asmp.getPartSlot()
    }
  }

  override fun getOutlineBoxes(world: World, pos: BlockPos, state: IBlockState): Set<AxisAlignedBB> {
    val hit = Minecraft.getMinecraft().objectMouseOver
    if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
      val te = world.getTileEntity(hit.blockPos)
      if (te is TileMultipartContainer) {
        val slotHit = MCMultiPart.slotRegistry.getValue(hit.subHit)
        te[slotHit].takeIf(Optional<*>::isPresent)?.let(Optional<IPartInfo>::get)?.also { part ->
          val qb = (part.tile as QBContainerTile).qb
          return qb.selectionBox
        }
      }
    }
    return super.getOutlineBoxes(world, pos, state)
  }

  override fun getOcclusionBoxes(part: IPartInfo): MutableList<AxisAlignedBB> {
    val qb = (part.tile as QBContainerTile).qb
    return qb.asmp.occlusionBoxes.toMutableList()
  }

  override fun getBoundingBox(part: IPartInfo): AxisAlignedBB? {
    val qb = (part.tile as QBContainerTile).qb
    return qb.rayCollisionBox
  }

  override fun addCollisionBoxToList(part: IPartInfo, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entity: Entity?, unknown: Boolean) {
    val qb = (part.tile as QBContainerTile).qb
    qb.collisionBox
      .map { it + part.partPos }
      .filter(entityBox::intersects)
      .also { collidingBoxes.addAll(it) }
  }

  override fun getDrops(world: IBlockAccess?, pos: BlockPos?, part: IPartInfo, fortune: Int): MutableList<ItemStack> {
    val qb = (part.tile as QBContainerTile).qb
    return qb.getDroppedItems().toMutableList()
  }

  /**
   * this is only used when placing the block
   */
  override fun getCollisionBoundingBox(world: World, pos: BlockPos, state: IBlockState?): AxisAlignedBB? {
    return buildPart(world, pos) {
      asmp.beforePlace(sidePlaced, placedX, placedY, placedZ)
      asmp.partPlacementBoundingBox
    }
  }

  override fun onPartChanged(part: IPartInfo, otherPart: IPartInfo) {
    (part.tile as QBContainerTile).qb.asmp.onPartChanged(otherPart)
  }

  override fun canPlacePartAt(world: World, pos: BlockPos) =
    canPlaceBlockAt(world, pos)

  override fun canPlacePartOnSide(world: World, pos: BlockPos, side: EnumFacing, slot: IPartSlot?) =
    canPlaceBlockOnSide(world, pos, side)

  override fun getLightOpacity(state: IBlockState?): Int = this.lightOpacity
  override fun getLightValue(state: IBlockState?): Int = this.lightValue

  private val QBlock.asmp: IQBlockMultipart
    get() = this as IQBlockMultipart
}