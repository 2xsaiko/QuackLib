package therealfarfetchd.quacklib.common.wires

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.client.render.wires.EnumWireRender
import therealfarfetchd.quacklib.common.DataTarget
import therealfarfetchd.quacklib.common.Scheduler
import therealfarfetchd.quacklib.common.extensions.*
import therealfarfetchd.quacklib.common.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.qblock.QBlock
import therealfarfetchd.quacklib.common.util.QNBTCompound

abstract class BlockWire(val width: Double, val height: Double) : QBlock(), IQBlockMultipart {

  val baseBounds = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, height, 1.0)
  val partBounds = AxisAlignedBB((1 - width) / 2, 0.0, (1 - width) / 2, 1 - (1 - width) / 2, height, 1 - (1 - width) / 2)
  val edgeBounds = AxisAlignedBB(-height, 0.0, (1 - width) / 2, 0.0, height, 1 - (1 - width) / 2)

  var facing: EnumFacing = EnumFacing.DOWN; protected set

  var connections: Map<Pair<EnumFacing, EnumFacing>, EnumWireConnection> = emptyMap()

  /**
   * Determine if this wire can connect to the passed BlockWire.
   */
  abstract fun connectsTo(other: BlockWire): Boolean

  private fun updateCableConnections() {
    if (world.isServer) {
      val oldconn = connections
      connections = validSides[facing]!!.map { it to facing to getConnectionOnSide(it) }.toMap()
      if (connections != oldconn) {
        dataChanged()
        clientDataChanged()
      }
    }
  }

  open fun getConnectionOnSide(f: EnumFacing): EnumWireConnection {
    var block: QBlock? = actualWorld.getQBlock(pos, EnumFaceSlot.fromFace(f))
    if (block is BlockWire && connectsTo(block) && block.connectsTo(this)) {
      return EnumWireConnection.Internal
    }

    block = world.getQBlock(pos.offset(f), EnumFaceSlot.fromFace(facing))
    if (block is BlockWire && connectsTo(block) && block.connectsTo(this)) {
      return EnumWireConnection.External
    }

    block = world.getQBlock(pos.offset(f).offset(facing), EnumFaceSlot.fromFace(f.opposite))
    if (block is BlockWire && connectsTo(block) && block.connectsTo(this)) {
      return EnumWireConnection.Corner
    }

    return EnumWireConnection.None
  }


  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    facing = sidePlaced
    updateCableConnections()
    container.notifyNeighborsOfSides(*validSides[facing]!!.toTypedArray())
  }

  override fun onBreakBlock() {
    super.onBreakBlock()
    Scheduler.schedule(0) {
      container.notifyNeighborsOfSides(*validSides[facing]!!.toTypedArray())
    }
  }

  private fun serializeConnections(): List<Byte> {
    var list: List<Int> = emptyList()
    for ((a, b) in connections.filterValues { it.renderType != EnumWireRender.Invisible }) {
      list += a.first.index
      list += a.second.index
      list += b.identifierId
    }
    return list.nibbles()
  }

  private fun deserializeConnections(list: List<Byte>) {
    var l = list.unpackNibbles()
    connections = emptyMap()
    while (l.size >= 3) {
      val a1 = EnumFacing.getFront(l[0])
      val a2 = EnumFacing.getFront(l[1])
      val b = EnumWireConnection.byIdentifier(l[2])
      connections += a1 to a2 to b
      l = l.slice(3 until l.size)
    }
  }

  private fun mapConnection(sideIn: Int): EnumWireConnection {
    val el = lookupMap[facing]!![sideIn]
    return connections[el to facing] ?: EnumWireConnection.None
  }

  override fun beforePlace(sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    facing = sidePlaced
  }

  override fun onNeighborChanged(side: EnumFacing) {
    super.onNeighborChanged(side)
    updateCableConnections()
  }

  override fun onNeighborTEChanged(side: EnumFacing) {
    super.onNeighborTEChanged(side)
    updateCableConnections()
  }

  override fun onPartChanged(part: IPartInfo) {
    super.onPartChanged(part)
    updateCableConnections()
  }

  override fun canStay(): Boolean {
    if (prePlaced) return true // we don't have the facing yet, so we're going to say yes
    return canBePlacedOnSide(facing)
  }

  override fun canBePlacedOnSide(side: EnumFacing): Boolean {
    val anchorPos = pos.offset(side)
    return world.getBlockState(anchorPos).isSideSolid(world, anchorPos, side.opposite)
  }

  override fun getBlockFaceShape(facing: EnumFacing): BlockFaceShape = BlockFaceShape.UNDEFINED

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    nbt.ubyte["F"] = facing.index
    if (!prePlaced) nbt.bytes["C"] = serializeConnections().toByteArray()
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    facing = EnumFacing.getFront(nbt.ubyte["F"])
    if (!prePlaced) deserializeConnections(nbt.bytes["C"].toList())
  }

  override fun canHarvestBlock(player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Boolean = true

  override fun blockStrength(player: EntityPlayer): Float = 1f / hardness / 30f

  override val selectionBox: AxisAlignedBB
    get() = baseBounds.rotate(facing)

  override val partPlacementBoundingBox: AxisAlignedBB?
    get() = partBounds.rotate(facing)

  override fun applyProperties(state: IBlockState): IBlockState = super.applyProperties(state).withProperty(PropFacing, facing)

  override fun applyExtendedProperties(state: IExtendedBlockState): IExtendedBlockState {
    return super.applyExtendedProperties(state).withProperty(PropConnections, (0..3).map(this::mapConnection))
  }

  override fun getPartSlot(): IPartSlot = EnumFaceSlot.fromFace(facing)
  override fun rotateBlock(axis: EnumFacing): Boolean = false
  override val properties: Set<IProperty<*>> = super.properties + PropFacing
  override val unlistedProperties: Set<IUnlistedProperty<*>> = super.unlistedProperties + PropConnections
  override val collisionBox: AxisAlignedBB? = null
  override val isFullBlock: Boolean = false
  override val hardness: Float = 0.25F
  override val material: Material = Material.IRON

  companion object {
    val PropFacing = PropertyEnum.create("facing", EnumFacing::class.java)!!
    val PropConnections = object : IUnlistedProperty<List<EnumWireConnection>> {
      @Suppress("UNCHECKED_CAST")
      override fun getType(): Class<List<EnumWireConnection>> = List::class.java as Class<List<EnumWireConnection>> // really kotlin?
      override fun getName(): String = "connections"
      override fun valueToString(value: List<EnumWireConnection>): String = value.toString()
      override fun isValid(value: List<EnumWireConnection>?): Boolean = true
    }

    val validSides = VALUES
      .map { f -> f to VALUES.filterNot { it.axis == f.axis }.toSet() }.toMap()

    val lookupMap = mapOf(
      DOWN to listOf(NORTH, EAST, SOUTH, WEST),
      UP to listOf(NORTH, EAST, SOUTH, WEST),
      NORTH to listOf(DOWN, EAST, UP, WEST),
      SOUTH to listOf(DOWN, EAST, UP, WEST),
      WEST to listOf(DOWN, NORTH, UP, SOUTH),
      EAST to listOf(DOWN, NORTH, UP, SOUTH)
    )

    fun collisionCheck(world: World, pos: BlockPos, bb: AxisAlignedBB): Boolean {
      if (world.isAirBlock(pos)) return true
      val state = world.getBlockState(pos)
      val list = ArrayList<AxisAlignedBB>()
      state.getActualState(world, pos).addCollisionBoxToList(world, pos, bb + pos, list, null, false)
      return list.isEmpty()
    }
  }
}