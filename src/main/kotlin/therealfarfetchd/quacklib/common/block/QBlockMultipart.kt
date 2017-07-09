package therealfarfetchd.quacklib.common.block

import mcmultipart.api.slot.IPartSlot
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing

/**
 * Created by marco on 09.07.17.
 */
abstract class QBlockMultipart : QBlock() {

  abstract fun getPartSlot(): IPartSlot

  abstract fun getPlacementSlot(facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot

}