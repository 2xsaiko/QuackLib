package therealfarfetchd.quacklib.common

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 08.07.17.
 */
open class QBContainerTile(val qb: QBlock) : TileEntity() {

  init {
    @Suppress("LeakingThis")
    qb.container = this
  }

  override fun setWorld(world: World) {
    super.setWorld(world)
    qb.world = world
  }

  override fun setPos(pos: BlockPos) {
    super.setPos(pos)
    qb.pos = pos
  }

  open class Ticking(qb: QBlock) : QBContainerTile(qb), ITickable {
    private val tickable = qb as ITickable

    override fun update() = tickable.update()
  }

}