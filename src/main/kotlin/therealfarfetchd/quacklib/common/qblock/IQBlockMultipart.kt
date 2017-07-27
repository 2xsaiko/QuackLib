package therealfarfetchd.quacklib.common.qblock

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.IPartSlot
import mcmultipart.util.MCMPWorldWrapper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World

/**
 * Created by marco on 09.07.17.
 */
interface IQBlockMultipart {

  private val qb: QBlock
    get() = this as QBlock

  val actualWorld: World
    get() {
      val world = qb.world
      if (world is MCMPWorldWrapper) return world.actualWorld
      return world
    }

  fun getPartSlot(): IPartSlot

  fun getPlacementSlot(facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot

  fun getPartPlacementBoundingBox(facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): AxisAlignedBB? = qb.collisionBox

  fun onPartChanged(part: IPartInfo): Unit {
    with(qb) {
      if (!canStay()) dismantle()
    }
  }

}