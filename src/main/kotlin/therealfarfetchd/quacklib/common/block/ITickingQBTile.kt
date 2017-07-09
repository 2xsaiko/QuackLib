package therealfarfetchd.quacklib.common.block

import net.minecraft.util.ITickable

/**
 * Created by marco on 09.07.17.
 */
interface ITickingQBTile : ITickable {

  val qb: QBlock

  override fun update() {
    @Suppress("SENSELESS_COMPARISON")
    if (qb != null) (qb as ITickable).update()
  }

}