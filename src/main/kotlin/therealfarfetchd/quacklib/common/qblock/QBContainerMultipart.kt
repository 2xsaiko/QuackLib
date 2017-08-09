package therealfarfetchd.quacklib.common.qblock

import mcmultipart.MCMultiPart
import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.IMultipartTile
import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.DataTarget
import therealfarfetchd.quacklib.common.extensions.getQBlock
import therealfarfetchd.quacklib.common.extensions.plus

/**
 * Created by marco on 09.07.17.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
open class QBContainerMultipart(rl: ResourceLocation, factory: () -> QBlock) : QBContainer(rl, factory), IMultipart {

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

  override fun getSelectedBoundingBox(state: IBlockState?, world: World, pos: BlockPos): AxisAlignedBB {
    val hit = Minecraft.getMinecraft().objectMouseOver
    if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
      val te = world.getTileEntity(hit.blockPos)
      if (te is TileMultipartContainer) {
        val slotHit = MCMultiPart.slotRegistry.getValue(hit.subHit)
        val infoOpt = te.get(slotHit)
        if (infoOpt.isPresent) {
          val part = infoOpt.get()
          val qb = (part.tile as QBContainerTile).qb
          return qb.selectionBox + pos
        }
      }
    }
    return super.getSelectedBoundingBox(state, world, pos)
  }

  override fun getOcclusionBoxes(part: IPartInfo): MutableList<AxisAlignedBB> {
    return mutableListOf(getBoundingBox(part) ?: AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
  }

  override fun getBoundingBox(part: IPartInfo): AxisAlignedBB? {
    val qb = (part.tile as QBContainerTile).qb
    return qb.rayCollisionBox
  }

  override fun addCollisionBoxToList(part: IPartInfo, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entity: Entity?, unknown: Boolean) {
    val qb = (part.tile as QBContainerTile).qb
    val blockBox = qb.collisionBox
    if (blockBox != NULL_AABB) {
      val axisalignedbb = blockBox!!.offset(part.partPos)
      if (entityBox.intersects(axisalignedbb)) {
        collidingBoxes.add(axisalignedbb)
      }
    }
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

  override fun getLightOpacity(state: IBlockState?): Int = this.lightOpacity
  override fun getLightValue(state: IBlockState?): Int = this.lightValue

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = factory()
    (worldIn ?: savedWorld)?.also { qb.world = it }
    savedPos?.also { qb.pos = it }
    qb.loadData(savedNbt, DataTarget.Save)
    if (qb is ITickable) return QBContainerTileMultipart.Ticking(qb)
    else return QBContainerTileMultipart(qb)
  }

  private val QBlock.asmp: IQBlockMultipart
    get() = this as IQBlockMultipart

}