package therealfarfetchd.quacklib.testmod

import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentRedstoneFace
import therealfarfetchd.quacklib.api.block.component.import
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.tools.Facing

class ComponentRedstone : BlockComponentRedstoneFace, BlockComponentDataImport {

  val facing = import<Facing>()

  override fun strongPowerLevel(block: Block, side: Facing): Int {
    val facing = block[facing]
    return if (facing == side) 15
    else 0
  }

  override fun weakPowerLevel(block: Block, side: Facing): Int {
    val facing = block[facing]
    return if (facing.opposite != side) 15
    else 0
  }

  override fun canConnectRedstone(block: Block, side: Facing): Boolean {
    val facing = block[facing]
    return side.axis != facing.axis
  }

  override fun side(block: Block): Facing = block[facing]

}