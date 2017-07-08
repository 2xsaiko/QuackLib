package therealfarfetchd.quacklib.common

import net.minecraft.block.material.Material
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 08.07.17.
 */
abstract class QBlock {

  /**
   * The block material. Must stay constant at all times.
   */
  abstract val material: Material

  /**
   * How long it takes to break this block.
   */
  open val hardness: Float = 1.0f

  /**
   * Returns the mouseover selection box of the block.
   */
  open val selectionBox: AxisAlignedBB
    get() = collisionBox

  /**
   * Returns the raytrace collision box of the block
   */
  open val rayCollisionBox: AxisAlignedBB
    get() = selectionBox

  /**
   * Returns the entity collision box of the block.
   */
  open val collisionBox: AxisAlignedBB = FullAABB

  /**
   * The tile entity this QBlock is in.
   */
  lateinit var container: QBContainerTile

  /**
   * The world this QBlock is in.
   */
  val world: World
    get() = container.world

  /**
   * The position this QBlock is at.
   */
  val pos: BlockPos
    get() = container.pos

  companion object {
    val FullAABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
  }

}