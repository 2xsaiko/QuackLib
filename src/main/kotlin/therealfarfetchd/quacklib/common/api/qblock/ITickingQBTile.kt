package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.util.ITickable

/**
 * Created by marco on 09.07.17.
 */
interface ITickingQBTile : ITickable {
  override fun update() {
    val qb = (this as QBContainerTile).qb
    @Suppress("SENSELESS_COMPARISON")
    if (qb != null) (qb as ITickable).update()
  }
}