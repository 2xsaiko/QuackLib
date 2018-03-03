//package therealfarfetchd.quacklib.common.api.wires
//
//import mcmultipart.api.container.IMultipartContainer
//import mcmultipart.api.slot.EnumFaceSlot
//import mcmultipart.block.TileMultipartContainer
//import net.minecraft.util.EnumFacing
//import net.minecraft.util.math.BlockPos
//import net.minecraft.world.World
//import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender
//import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
//import therealfarfetchd.quacklib.common.api.block.capability.IConnectable
//import therealfarfetchd.quacklib.common.api.extensions.bothNotNull
//import therealfarfetchd.quacklib.common.api.extensions.isServer
//import therealfarfetchd.quacklib.common.api.extensions.nibbles
//import therealfarfetchd.quacklib.common.api.extensions.unpackNibbles
//import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultipart
//import therealfarfetchd.quacklib.common.api.qblock.QBlock
//import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation
//import therealfarfetchd.quacklib.common.api.util.WireCollisionHelper
//import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.*
//
//interface BaseConnectable {
//  private val qb: QBlock
//    get() = this as QBlock
//
//  private val QBlock.actualWorld: World
//    get() = if (this is IQBlockMultipart) actualWorld else world
//
//  var connections: Map<EnumFaceLocation, EnumWireConnection>
//
//  fun updateCableConnections(): Boolean {
//    var changed = false
//    if (qb.world.isServer) {
//      val oldconn = connections
//      connections = validEdges.map { edge ->
//        edge to (Internal.takeIf { edge.side != null && checkBlock(qb.pos, edge, edge.side, edge.base, it) }
//                 ?: External.takeIf { checkBlock(qb.pos.offset(edge.base), edge, edge.base, edge.side, it) }
//                 ?: Corner.takeIf { edge.side != null && checkBlock(qb.pos.offset(edge.base).offset(edge.side), edge, edge.side, edge.base.opposite, it) }
//                 ?: None)
//      }.toMap()
//      if (connections != oldconn) {
//        qb.dataChanged()
//        changed = true
//      }
//    }
//    return changed
//  }
//
//  fun getNeighbor(l: EnumFaceLocation): Any? {
//    return when (connections[l]) {
//      Internal -> if (l.side != null) getBlock(qb.pos, l.side, l.base) else null
//      External -> getBlock(qb.pos.offset(l.base), l.base, l.side)
//      Corner   -> if (l.side != null) getBlock(qb.pos.offset(l.base).offset(l.side), l.side, l.base.opposite) else null
//      else     -> null
//    }
//  }
//
//  private fun checkBlock(pos: BlockPos, e: EnumFaceLocation, f1: EnumFacing, f2: EnumFacing?, c: EnumWireConnection): Boolean {
//    return when {
//      c == Corner && WireCollisionHelper.collides(qb.world, qb.pos.offset(e.base), e)  -> false
//      c == Internal && (qb.actualWorld.getTileEntity(qb.pos) !is IMultipartContainer
//        /*|| WireCollisionHelper.collidesInternal(qb.actualWorld, qb.pos, f1, f2!!)*/) -> false
//      c == External && connectsToOther(qb.pos, e)                                      -> true
//      else                                                                             -> {
//        val (cap, localCap) = getCap(pos, e, f1, c) ?: return false
//        val a1 = cap.getEdge(f2) ?: return false
//        val a2 = localCap.getEdge(e.side) ?: return false
//        cap.getType(f2) == localCap.getType(e.side) &&
//        checkConnection(e) &&
//        checkAdditional(a1, a2) &&
//        (c != Corner || (cap.allowCornerConnections(f2) || localCap.allowCornerConnections(e.side)))
//      }
//    }
//  }
//
//  private fun getCap(pos: BlockPos, e: EnumFaceLocation, f1: EnumFacing, c: EnumWireConnection) = when (c) {
//    EnumWireConnection.Internal -> (qb.actualWorld.getTileEntity(qb.pos) as? TileMultipartContainer)?.let { te ->
//      Pair(
//        te.getPartTile(EnumFaceSlot.fromFace(e.base)).map { it.getPartCapability(Capabilities.Connectable, f1.opposite) }.orElse(null),
//        qb.getCapability(Capabilities.Connectable, e.base)
//      )
//    }
//    else                        -> Pair(
//      qb.actualWorld.getTileEntity(pos)?.getCapability(Capabilities.Connectable, f1.opposite),
//      qb.getCapability(Capabilities.Connectable, e.base)
//    )
//  }?.bothNotNull()
//
//
//  /**
//   * Allows additional checks to determine if it should connect
//   */
//  fun checkAdditional(cap: Any?, localCap: Any?): Boolean = true
//
//  /**
//   * Allows a method to connect to non IConnectable blocks (e.g redstone dust)
//   */
//  fun connectsToOther(thisBlock: BlockPos, e: EnumFaceLocation): Boolean = false
//
//  /**
//   * Allows additional checks to determine if it should connect
//   */
//  fun checkConnection(e: EnumFaceLocation) = true
//
//  private fun getBlock(pos: BlockPos, f1: EnumFacing, f2: EnumFacing?): Any? {
//    val cap: IConnectable = qb.actualWorld.getTileEntity(pos)?.getCapability(Capabilities.Connectable, f1.opposite)
//                            ?: return false
//    cap.getEdge(f2) ?: return null
//    return cap.getEdge(f2)
//  }
//
//  fun serializeConnections(): List<Byte> {
//    var list: List<Int> = emptyList()
//    for ((a, b) in connections.filterValues { it.renderType != EnumWireRender.Invisible }) {
//      list += a.base.index
//      list += a.side?.index ?: 6
//      list += b.identifierId
//    }
//    return list.nibbles()
//  }
//
//  fun deserializeConnections(list: List<Byte>) {
//    var l = list.unpackNibbles()
//    connections = emptyMap()
//    while (l.size >= 3) {
//      val a1 = EnumFacing.getFront(l[0])
//      val a2 = if (l[1] == 6) null else EnumFacing.getFront(l[1])
//      val b = EnumWireConnection.byIdentifier(l[2])
//      connections += EnumFaceLocation.fromFaces(a1, a2) to b
//      l = l.slice(3 until l.size)
//    }
//  }
//
//  val validEdges: Set<EnumFaceLocation>
//    get() = EnumFaceLocation.Values.filter { qb.getCapability(Capabilities.Connectable, it.base)?.getEdge(it.side) != null }.toSet()
//}