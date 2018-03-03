package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended
import therealfarfetchd.quacklib.common.api.util.QNBTCompound
import therealfarfetchd.quacklib.common.api.util.Scheduler
import therealfarfetchd.quacklib.common.api.wires.*

abstract class QBlockConnectable : QBlock(), BaseConnectable2 {
  override var connections: Map<EnumFacingExtended, EnumWireConnection> = emptyMap()

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    updateCableConnections()
    notifyWires()
  }

  override fun onBreakBlock() {
    super.onBreakBlock()
    Scheduler.schedule(0) { notifyWires() }
  }

  protected fun notifyWires() {
    for (edge in validEdges) {
      if (edge.isVertical) continue
      val p = edge.part?.let { pos.offset(it) } ?: pos
      val q = p.offset(edge.direction)
      world.neighborChanged(q, container.blockType, p)
    }
  }

  override fun onNeighborChanged(side: EnumFacing) {
    super.onNeighborChanged(side)
    updateCableConnections()
  }

  override fun onNeighborTEChanged(side: EnumFacing) {
    super.onNeighborTEChanged(side)
    updateCableConnections()
  }

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    if (!prePlaced) nbt.bytes["C"] = serializeConnections().toByteArray()
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    if (!prePlaced) deserializeConnections(nbt.bytes["C"].toList())
  }
}