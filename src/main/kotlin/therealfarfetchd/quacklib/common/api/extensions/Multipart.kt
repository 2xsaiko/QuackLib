package therealfarfetchd.quacklib.common.api.extensions

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.SlotUtil
import mcmultipart.block.TileMultipartContainer
import mcmultipart.multipart.PartInfo
import net.minecraft.util.EnumFacing
import java.util.function.Function

fun TileMultipartContainer.canConnectRedstone(side: EnumFacing, filter: (IPartInfo) -> Boolean = { true }) =
  SlotUtil.viewContainer(this, Function { filter(it) && it.part.canConnectRedstone((it as PartInfo).wrapAsNeeded(world), pos, it, side) },
    Function { it.any { it } }, false, true, side.opposite) ?: false

fun TileMultipartContainer.getWeakPower(side: EnumFacing, filter: (IPartInfo) -> Boolean = { true }) =
  SlotUtil.viewContainer<IPartInfo, Int>(this,
    Function { i -> i.part.getWeakPower((i as PartInfo).wrapAsNeeded(world), pos, i, side).takeIf { filter(i) } ?: 0 },
    Function { l -> l.max() ?: 0 }, 0, true, side.opposite) ?: 0

fun TileMultipartContainer.getStrongPower(side: EnumFacing, filter: (IPartInfo) -> Boolean = { true }) =
  SlotUtil.viewContainer<IPartInfo, Int>(this,
    Function { i -> i.part.getStrongPower((i as PartInfo).wrapAsNeeded(world), pos, i, side).takeIf { filter(i) } ?: 0 },
    Function { l -> l.max() ?: 0 }, 0, true, side.opposite) ?: 0