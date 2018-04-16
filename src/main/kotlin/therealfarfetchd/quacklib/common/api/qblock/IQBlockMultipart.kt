package therealfarfetchd.quacklib.common.api.qblock

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.IPartSlot
import mcmultipart.util.MCMPWorldWrapper
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.wires.TileConnectable

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

  /**
   * Gets called before the block is placed in the world - useful for setting state based on the parameters
   */
  fun beforePlace(sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {}

  val partPlacementBoundingBox: AxisAlignedBB?
    get() = qb.collisionBox.reduce(AxisAlignedBB::union)

  val occlusionBoxes: List<AxisAlignedBB>
    get() = listOfNotNull(partPlacementBoundingBox)

  fun onPartChanged(part: IPartInfo) {
    with(qb) {
      if (!canStay()) dismantle()
      else if (this is TileConnectable) getConnectionResolver().updateCableConnections()
    }
  }
}