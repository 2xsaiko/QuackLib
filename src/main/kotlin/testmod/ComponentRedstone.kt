package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentRedstoneFace
import therealfarfetchd.quacklib.api.block.component.ImportedData
import therealfarfetchd.quacklib.api.block.data.BlockDataRO

class ComponentRedstone : BlockComponentRedstoneFace, BlockComponentDataImport<ComponentRedstone, ComponentRedstone.Imported> {

  override val imported = Imported(this)

  override fun strongPowerLevel(data: BlockDataRO, side: EnumFacing): Int {
    val facing = data[imported.facing]
    return if (facing == side) 15
    else 0
  }

  override fun weakPowerLevel(data: BlockDataRO, side: EnumFacing): Int {
    val facing = data[imported.facing]
    return if (facing.opposite != side) 15
    else 0
  }

  override fun canConnectRedstone(data: BlockDataRO, side: EnumFacing): Boolean {
    val facing = data[imported.facing]
    return side.axis != facing.axis
  }

  override fun side(data: BlockDataRO): EnumFacing = data[imported.facing]

  class Imported(target: ComponentRedstone) : ImportedData<Imported, ComponentRedstone>(target) {

    val facing = import<EnumFacing>()

  }

}