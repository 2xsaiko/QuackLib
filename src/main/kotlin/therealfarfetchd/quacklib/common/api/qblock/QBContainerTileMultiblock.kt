package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import therealfarfetchd.quacklib.common.api.extensions.minus
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

open class QBContainerTileMultiblock() : QBContainerTile() {
  val qbmb by etype<IQBlockMultiblock>()

  var extBlocks: Collection<BlockPos> = emptyList()

  constructor(qbIn: QBlock) : this() {
    setQBChecked(qbIn, IQBlockMultiblock::class)
  }

  override fun onLoad() {
    if (!bits[0]) qbmb.fillBlocks()
    super.onLoad()
  }

  override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
    saveExtBlocks(QNBTCompound(compound))
    return super.writeToNBT(compound)
  }

  override fun readFromNBT(compound: NBTTagCompound) {
    super.readFromNBT(compound)
    loadExtBlocks(QNBTCompound(compound))
  }

  private fun saveExtBlocks(nbt: QNBTCompound) {
    nbt.longs["P"] = extBlocks.map { (it + pos).toLong() }.toLongArray()
  }

  private fun loadExtBlocks(nbt: QNBTCompound) {
    extBlocks = nbt.longs["P"].map { BlockPos.fromLong(it) - pos }
  }

  open class Ticking() : QBContainerTileMultiblock(), ITickingQBTile {
    constructor(qbIn: QBlock) : this() {
      setQBChecked(qbIn, IQBlockMultiblock::class)
    }
  }
}