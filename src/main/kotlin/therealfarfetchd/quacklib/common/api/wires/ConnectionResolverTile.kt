package therealfarfetchd.quacklib.common.api.wires

import mcmultipart.api.slot.EnumCenterSlot
import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender
import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
import therealfarfetchd.quacklib.common.api.extensions.isServer
import therealfarfetchd.quacklib.common.api.extensions.nibbles
import therealfarfetchd.quacklib.common.api.extensions.unpackNibbles
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended

open class ConnectionResolverTile(val tc: TileConnectable) {
  private val te
    get() = tc.getTile()

  var connections: Map<EnumFacingExtended, EnumWireConnection> = emptyMap()

  fun updateCableConnections(): Boolean {
    var changed = false
    if (te.world.isServer) {
      val oldconn = connections

      connections = EnumFacingExtended.Values
        .filter(tc::pass1FilterConnections)
        .associate { it to associateConnectionType(it) }

      if (connections != oldconn) {
        tc.connectionsChanged()
        changed = true
      }
    }
    return changed
  }

  fun serializeConnections(): List<Byte> {
    var list: List<Int> = emptyList()
    for ((a, b) in connections.filterValues { it.renderType != EnumWireRender.Invisible }) {
      list += a.part?.index ?: 6
      list += a.direction.index
      list += b.identifierId
    }
    return list.nibbles()
  }

  fun deserializeConnections(list: List<Byte>) {
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

  private fun associateConnectionType(e: EnumFacingExtended): EnumWireConnection = associateConnectionType(e, ::testConnection)

  private fun associateConnectionType(e: EnumFacingExtended, op: (EnumFacingExtended, EnumWireConnection) -> Boolean): EnumWireConnection {
    return listOf(EnumWireConnection.Internal, EnumWireConnection.External, EnumWireConnection.Corner).firstOrNull { op(e, it) }
           ?: EnumWireConnection.None
  }

  private fun testConnectionLocal(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
    val mp = tc.getWorldForScan().getTileEntity(te.pos) as? TileMultipartContainer
    if (mp != null && tc.collideParts(mp, wc, e)) return false
    if (wc == EnumWireConnection.Corner && tc.collideCorner(e)) return false
    return true
  }

  private fun testConnectionExt(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
    if (!testConnectionLocal(e, wc)) return false
    if (tc.forceConnectTo(e, wc)) return true
    val ne = e.getOpposite(wc) ?: return false
    val np = te.pos.getOpposite(e, wc)
    val nte = getTile(np, ne.getPartSlot()) ?: return false
    if (te == nte) return false

    val cap = te.getConnectableCap(e) ?: return false
    val nCap = nte.getConnectableCap(ne) ?: return false

    if (cap.getType(e.part.takeUnless { e.isVertical }) != nCap.getType(ne.part.takeUnless { ne.isVertical })) return false
    return true
  }

  fun getNeighbor(e: EnumFacingExtended): Any? {
    val c = connections[e]
    if (c in setOf(EnumWireConnection.None, null)) return null; c!!
    val op = te.pos.getOpposite(e, c)
    val ne = e.getOpposite(c)
    val partSlot = ne?.getPartSlot() ?: return null
    return getTile(op, partSlot)?.getConnectable(ne)
  }

  private fun EnumFacingExtended.getPartSlot(): IPartSlot = when (part) {
    null -> EnumCenterSlot.CENTER
    else -> EnumFaceSlot.fromFace(part)
  }

  private fun testConnection(e: EnumFacingExtended, wc: EnumWireConnection): Boolean {
    if (!testConnectionLocal(e, wc)) return false
    if (tc.forceConnectTo(e, wc)) return true
    val ne = e.getOpposite(wc) ?: return false
    val np = te.pos.getOpposite(e, wc)
    val nte = getTile(np, ne.getPartSlot()) ?: return false
    if (nte == te) return false

    val cap = te.getConnectableCap(e) ?: return false
    val nCap = nte.getConnectableCap(ne) ?: return false

    val ePart = e.part.takeUnless { e.isVertical }
    val t = cap.getType(ePart) ?: return false
    val c = cap.getEdge(ePart)

    val nePart = ne.part.takeUnless { ne.isVertical }
    val nt = nCap.getType(nePart) ?: return false
    val nc = nCap.getEdge(nePart)

    if (t != nt) return false

    if (!tc.canConnectTo(t, c, nt, nc)) return false

    val ntc = nte.getCapability(TileConnectable.Capability, null)
    if (nte is TileConnectable) {
      val ayy = associateConnectionType(ne, nte.getConnectionResolver()::testConnectionExt)
      if (ayy != wc) return false
      if (!nte.canConnectTo(nt, nc, t, c)) return false
    }
    return true
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

  private fun getTile(pos: BlockPos, slot: IPartSlot): TileEntity? {
    val world = tc.getWorldForScan()
    val te = world.getTileEntity(pos) ?: return null
    val part = MultipartRegistry.INSTANCE.getPart(te.blockType)
    if (part != null) {
      return if (part.getSlotFromWorld(world, pos, world.getBlockState(pos)) == slot) te else null
    }
    if (te is TileMultipartContainer) {
      return te.getPartTile(slot).orElse(null)?.tileEntity
    }
    return te
  }
}

interface TileConnectable {
  fun getConnectionResolver(): ConnectionResolverTile

  fun getTile(): TileEntity

  fun connectionsChanged() {
    getTile().markDirty()
  }

  fun getWorldForScan(): World

  fun pass1FilterConnections(e: EnumFacingExtended): Boolean = getTile().getConnectable(e) != null

  fun collideParts(mp: TileMultipartContainer, wc: EnumWireConnection, e: EnumFacingExtended): Boolean = false

  fun collideCorner(e: EnumFacingExtended): Boolean = false

  fun canConnectTo(t: ResourceLocation, c: Any?, nt: ResourceLocation, nc: Any?): Boolean = true

  fun forceConnectTo(e: EnumFacingExtended, wc: EnumWireConnection): Boolean = false

  companion object {
    val Capability
      get() = Capabilities.TileConnectable
  }
}

fun TileConnectable.getNeighbor(e: EnumFacingExtended): Any? = getConnectionResolver().getNeighbor(e)

private fun TileEntity.getConnectableCap(e: EnumFacingExtended) = getCapability(Capabilities.Connectable, e.direction)

private fun TileEntity.getConnectable(e: EnumFacingExtended) = getConnectableCap(e)?.getEdge(e.part.takeUnless { e.isVertical })

val TileConnectable.validEdges
  get() = EnumFacingExtended.Values.filter(::pass1FilterConnections).toSet()