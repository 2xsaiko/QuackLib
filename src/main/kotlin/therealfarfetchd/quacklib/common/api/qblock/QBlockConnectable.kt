package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound
import therealfarfetchd.quacklib.common.api.util.Scheduler
import therealfarfetchd.quacklib.common.api.wires.ConnectionResolverTile
import therealfarfetchd.quacklib.common.api.wires.TileConnectable
import therealfarfetchd.quacklib.common.api.wires.validEdges

abstract class QBlockConnectable : QBlock(), TileConnectable {
  @Suppress("LeakingThis")
  protected var cr = ConnectionResolverTile(this)

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    cr.updateCableConnections()
    Scheduler.schedule(0) { notifyWires() }
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
    cr.updateCableConnections()
  }

  override fun onNeighborTEChanged(side: EnumFacing) {
    super.onNeighborTEChanged(side)
    cr.updateCableConnections()
  }

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    if (!prePlaced) nbt.bytes["C"] = cr.serializeConnections().toByteArray()
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    if (!prePlaced) cr.deserializeConnections(nbt.bytes["C"].toList())
  }

  override fun getConnectionResolver() = cr

  override fun getTile() = container

  override fun getWorldForScan() = if (this is IQBlockMultipart) actualWorld else world

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    return if (capability == TileConnectable.Capability) this as T
    else super.getCapability(capability, side)
  }
}