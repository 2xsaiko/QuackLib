package therealfarfetchd.quacklib.testmod

import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentRedstoneFace
import therealfarfetchd.quacklib.api.block.component.ImportedData
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.tools.Facing

class ComponentRedstone : BlockComponentRedstoneFace, BlockComponentDataImport<ComponentRedstone, ComponentRedstone.Imported> {

  override val imported = Imported(this)

  override fun strongPowerLevel(block: Block, side: Facing): Int {
    val facing = block[imported.facing]
    return if (facing == side) 15
    else 0
  }

  override fun weakPowerLevel(block: Block, side: Facing): Int {
    val facing = block[imported.facing]
    return if (facing.opposite != side) 15
    else 0
  }

  override fun canConnectRedstone(block: Block, side: Facing): Boolean {
    val facing = block[imported.facing]
    return side.axis != facing.axis
  }

  override fun side(block: Block): Facing = block[imported.facing]

  class Imported(target: ComponentRedstone) : ImportedData<Imported, ComponentRedstone>(target) {

    val facing = import<Facing>()

  }

}