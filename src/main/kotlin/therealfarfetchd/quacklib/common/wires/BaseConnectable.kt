package therealfarfetchd.quacklib.common.wires

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.client.render.wires.EnumWireRender
import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
import therealfarfetchd.quacklib.common.api.block.capability.IConnectable
import therealfarfetchd.quacklib.common.extensions.isServer
import therealfarfetchd.quacklib.common.extensions.nibbles
import therealfarfetchd.quacklib.common.extensions.unpackNibbles
import therealfarfetchd.quacklib.common.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.qblock.QBlock
import therealfarfetchd.quacklib.common.util.EnumFaceLocation
import therealfarfetchd.quacklib.common.util.WireCollisionHelper

interface BaseConnectable {

  var connections: Map<EnumFaceLocation, EnumWireConnection>

  fun updateCableConnections(): Boolean {
    var changed = false
    if (b.world.isServer) {
      val oldconn = connections
      connections = validEdges.map { edge ->
        edge to (EnumWireConnection.Internal.takeIf { edge.side != null && checkBlock(b.pos, edge, edge.side, edge.base) } ?:
                 EnumWireConnection.External.takeIf { checkBlock(b.pos.offset(edge.base), edge, edge.base, edge.side) } ?:
                 EnumWireConnection.Corner.takeIf { edge.side != null && checkBlock(b.pos.offset(edge.base).offset(edge.side), edge, edge.side.opposite, edge.base.opposite, true) } ?:
                 EnumWireConnection.None)
      }.toMap()
      if (connections != oldconn) {
        b.dataChanged()
        changed = true
      }
    }
    return changed
  }

  private fun checkBlock(pos: BlockPos, e: EnumFaceLocation, f1: EnumFacing, f2: EnumFacing?, corner: Boolean = false): Boolean {
    if (corner && WireCollisionHelper.collides(b.world, b.pos.offset(e.base), e)) return false
    val cap: IConnectable = b.actualWorld.getTileEntity(pos)?.getCapability(Capabilities.Connectable, f1.opposite) ?: return false
    val localCap: IConnectable = b.getCapability(Capabilities.Connectable, e.base) ?: return false
    cap.getEdge(f2) ?: return false
    localCap.getEdge(e.side) ?: return false
    return cap.getType(f2) == localCap.getType(e.side) && (!corner || (cap.allowCornerConnections(f2) || localCap.allowCornerConnections(e.side)))
  }

  fun serializeConnections(): List<Byte> {
    var list: List<Int> = emptyList()
    for ((a, b) in connections.filterValues { it.renderType != EnumWireRender.Invisible }) {
      list += a.base.index
      list += a.side?.index ?: 6
      list += b.identifierId
    }
    return list.nibbles()
  }

  fun deserializeConnections(list: List<Byte>) {
    var l = list.unpackNibbles()
    connections = emptyMap()
    while (l.size >= 3) {
      val a1 = EnumFacing.getFront(l[0])
      val a2 = if (l[1] == 6) null else EnumFacing.getFront(l[1])
      val b = EnumWireConnection.byIdentifier(l[2])
      connections += EnumFaceLocation.fromFaces(a1, a2) to b
      l = l.slice(3 until l.size)
    }
  }

  val validEdges: Set<EnumFaceLocation>
    get() = EnumFaceLocation.Values.filter { b.getCapability(Capabilities.Connectable, it.base)?.getEdge(it.side) != null }.toSet()

}

private val BaseConnectable.b: QBlock
  get() = this as QBlock

private val QBlock.actualWorld: World
  get() = if (this is IQBlockMultipart) actualWorld else world