package therealfarfetchd.quacklib.common

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable

/**
 * Created by marco on 08.07.17.
 */
open class QBContainerTile(val qb: QBlock) : TileEntity() {

  open class Ticking(qb: QBlock) : QBContainerTile(qb), ITickable {
    override fun update() {

    }
  }

}