package therealfarfetchd.quacklib.common.api.wires

import mcmultipart.api.slot.EnumCenterSlot
import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender
import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.extensions.isServer
import therealfarfetchd.quacklib.common.api.extensions.nibbles
import therealfarfetchd.quacklib.common.api.extensions.unpackNibbles
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended
import therealfarfetchd.quacklib.common.api.wires.EnumWireConnection.*
import kotlin.collections.component1
import kotlin.collections.component2

interface BaseConnectable2 {
  var connections: Map<EnumFacingExtended, EnumWireConnection>

  fun updateCableConnections(): Boolean {
    var changed = false
    if (qb.world.isServer) {
      val oldconn = connections

      connections = EnumFacingExtended.Values
        .filter(::pass1FilterConnections)
        .associate { it to associateConnectionType(it) }

      if (connections != oldconn) {
        qb.dataChanged()
        changed = true
      }
    }
    return changed
  }

  fun pass1FilterConnections(e: EnumFacingExtended): Boolean = qb.getConnectable(e) != null

  fun collideParts(mp: TileMultipartContainer, wc: EnumWireConnection, e: EnumFacingExtended): Boolean = false

  fun collideCorner(e: EnumFacingExtended): Boolean = false

  fun canConnectTo(t: ResourceLocation, c: Any?, nt: ResourceLocation, nc: Any?): Boolean = true

  fun forceConnectTo(e: EnumFacingExtended, wc: EnumWireConnection): Boolean = false
}

private val BaseConnectable2.qb: QBlock
  get() = this as QBlock

private val QBlock.actualWorld: World
  get() = if (this is IQBlockMultipart) actualWorld else world

val BaseConnectable2.validEdges
  get() = EnumFacingExtended.Values.filter(::pass1FilterConnections).toSet()

private fun BaseConnectable2.associateConnectionType(e: EnumFacingExtended): EnumWireConnection = associateConnectionType(e, ::testConnection)

private fun associateConnectionType(e: EnumFacingExtended, op: (EnumFacingExtended, EnumWireConnection) -> Boolean): EnumWireConnection {
  return listOf(Internal, External, Corner).firstOrNull { op(e, it) } ?: None
}

private fun BaseConnectable2.testConnectionLocal(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
  val mp = qb.actualWorld.getTileEntity(qb.pos) as? TileMultipartContainer
  if (mp != null && collideParts(mp, wc, e)) return false
  if (wc == Corner && collideCorner(e)) return false
  return true
}

private fun BaseConnectable2.testConnectionExt(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
  if (!testConnectionLocal(e, wc)) return false
  if (forceConnectTo(e, wc)) return true
  val ne = e.getOpposite(wc) ?: return false
  val np = qb.pos.getOpposite(e, wc)
  val nqb = qb.actualWorld.getQBlock1(np, ne.getPartSlot()) ?: return false
  if (qb == nqb) return false

  val cap = qb.getConnectableCap(e) ?: return false
  val nCap = nqb.getConnectableCap(ne) ?: return false

  if (cap.getType(e.part.takeUnless { e.isVertical }) != nCap.getType(ne.part.takeUnless { ne.isVertical })) return false
  return true
}

fun BaseConnectable2.getNeighbor(e: EnumFacingExtended): Any? {
  val c = connections[e]
  if (c in setOf(None, null)) return null; c!!
  val op = qb.pos.getOpposite(e, c)
  val ne = e.getOpposite(c)
  val partSlot = ne?.getPartSlot() ?: return null
  return qb.actualWorld.getQBlock1(op, partSlot)?.getConnectable(ne)
}

private fun EnumFacingExtended.getPartSlot(): IPartSlot = when (part) {
  null -> EnumCenterSlot.CENTER
  else -> EnumFaceSlot.fromFace(part)
}

private fun BaseConnectable2.testConnection(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
  if (!testConnectionLocal(e, wc)) return false
  if (forceConnectTo(e, wc)) return true
  val ne = e.getOpposite(wc) ?: return false
  val np = qb.pos.getOpposite(e, wc)
  val nqb = qb.actualWorld.getQBlock1(np, ne.getPartSlot()) ?: return false
  if (nqb == qb) return false

  val cap = qb.getConnectableCap(e) ?: return false
  val nCap = nqb.getConnectableCap(ne) ?: return false

  val ePart = e.part.takeUnless { e.isVertical }
  val t = cap.getType(ePart) ?: return false
  val c = cap.getEdge(ePart)

  val nePart = ne.part.takeUnless { ne.isVertical }
  val nt = nCap.getType(nePart) ?: return false
  val nc = nCap.getEdge(nePart)

  if (t != nt) return false

  if (!canConnectTo(t, c, nt, nc)) return false

  if (nqb is BaseConnectable2) {
    val ayy = associateConnectionType(ne, nqb::testConnectionExt)
    if (ayy != wc) return false
    if (!nqb.canConnectTo(nt, nc, t, c)) return false
  }
  return true
}

private fun QBlock.getConnectableCap(e: EnumFacingExtended) = getCapability(Capabilities.Connectable, e.direction)

private fun QBlock.getConnectable(e: EnumFacingExtended) = getConnectableCap(e)?.getEdge(e.part.takeUnless { e.isVertical })

fun BaseConnectable2.serializeConnections(): List<Byte> {
  var list: List<Int> = emptyList()
  for ((a, b) in connections.filterValues { it.renderType != EnumWireRender.Invisible }) {
    list += a.part?.index ?: 6
    list += a.direction.index
    list += b.identifierId
  }
  return list.nibbles()
}

fun BaseConnectable2.deserializeConnections(list: List<Byte>) {
  var l = list.unpackNibbles()
  connections = emptyMap()
  while (l.size >= 3) {
    val a1 = if (l[0] == 6) null else EnumFacing.getFront(l[0])
    val a2 = EnumFacing.getFront(l[1])
    val b = EnumWireConnection.byIdentifier(l[2])
    connections += EnumFacingExtended.fromFaces(a1, a2) to b
    l = l.slice(3 until l.size)
  }
}

private fun EnumFacingExtended.getOpposite(wc: EnumWireConnection) = when (wc) {
  EnumWireConnection.None     -> error("Invalid connection type $wc")
  EnumWireConnection.External -> oppositeExternal
  EnumWireConnection.Internal -> oppositeInternal
  EnumWireConnection.Corner   -> oppositeCorner
}

private fun BlockPos.getOpposite(e: EnumFacingExtended, wc: EnumWireConnection): BlockPos = when (wc) {
  EnumWireConnection.None     -> error("Invalid connection type $wc")
  EnumWireConnection.External -> offset(e.direction)
  EnumWireConnection.Internal -> this
  EnumWireConnection.Corner   -> offset(e.direction).offset(e.part!!)
}

private fun World.getQBlock1(pos: BlockPos, slot: IPartSlot): QBlock? = getQBlock(pos, slot)
                                                                        ?: getQBlock(pos) //?.takeIf { it !is IQBlockMultipart }