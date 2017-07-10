package therealfarfetchd.quacklib.common.qblock

import mcmultipart.api.slot.IPartSlot
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB

/**
 * Created by marco on 09.07.17.
 */
interface IQBlockMultipart {

  fun getPartSlot(): IPartSlot

  fun getPlacementSlot(facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot

  fun getPartPlacementBoundingBox(facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): AxisAlignedBB? = (this as QBlock).collisionBox

}