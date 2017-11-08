package therealfarfetchd.quacklib.common.api.wires

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
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import therealfarfetchd.quacklib.client.api.render.wires.EnumWireRender
import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
import therealfarfetchd.quacklib.common.api.block.capability.WireConnectable
import therealfarfetchd.quacklib.common.api.extensions.rotate
import therealfarfetchd.quacklib.common.api.extensions.rotateY
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.api.qblock.QBlockConnectable
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

abstract class BlockWire<out T>(width: Double, height: Double) : QBlockConnectable(), IQBlockMultipart {
  val baseBounds = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, height, 1.0)
  val partBounds = AxisAlignedBB((1 - width) / 2, 0.0, (1 - width) / 2, 1 - (1 - width) / 2, height, 1 - (1 - width) / 2)
  val edgeBounds = AxisAlignedBB(-height, 0.0, (1 - width) / 2, 0.0, height, 1 - (1 - width) / 2)
  val extBounds = AxisAlignedBB(0.0, 0.0, (1 - width) / 2, (1 - width) / 2, height, 1 - (1 - width) / 2)
  val extNBounds = AxisAlignedBB(0.25, 0.0, (1 - width) / 2, (1 - width) / 2, height, 1 - (1 - width) / 2)

  @Suppress("LeakingThis")
  private val connectable = WireConnectable(this)

  var facing: EnumFacing = EnumFacing.DOWN; protected set

  abstract val dataType: ResourceLocation

  abstract val data: T

  override fun updateCableConnections(): Boolean {
    return if (super.updateCableConnections()) {
      clientDataChanged()
      true
    } else false
  }

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    facing = sidePlaced
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
  }

  private fun mapConnection(sideIn: Int): EnumWireConnection {
    val el = lookupMap[facing]!![sideIn]
    return connections[EnumFaceLocation.fromFaces(el, facing)] ?: EnumWireConnection.None
  }

  private fun mapConnection2(sideIn: Int): EnumWireConnection {
    val el = lookupMap2[facing]!![sideIn]
    return connections[EnumFaceLocation.fromFaces(el, facing)] ?: EnumWireConnection.None
  }

  override fun beforePlace(sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    facing = sidePlaced
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
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    facing = EnumFacing.getFront(nbt.ubyte["F"])
  }

  override fun canHarvestBlock(player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Boolean = true

  override fun blockStrength(player: EntityPlayer): Float = 1f / hardness / 30f

  override val selectionBox: Set<AxisAlignedBB>
    get() {
      var set = listOf(partBounds)
      val ext = (0..3).filter { mapConnection2(it).renderType != EnumWireRender.Invisible }
      for (i in ext)
        set += extBounds.rotateY(EnumFacing.getHorizontal(i))
      if (ext.size == 1) {
        set += extNBounds.rotateY(getHorizontal((ext.first() + 2) % 4))
      } else if (ext.isEmpty()) {
        set += extNBounds.rotateY(NORTH)
        set += extNBounds.rotateY(SOUTH)
      }
      return set.map { it.rotate(facing) }.toSet()
    }

  override val partPlacementBoundingBox: AxisAlignedBB?
    get() = partBounds.rotate(facing)

  override fun applyProperties(state: IBlockState): IBlockState = super.applyProperties(state).withProperty(PropFacing, facing)

  override fun applyExtendedProperties(state: IExtendedBlockState): IExtendedBlockState {
    return super.applyExtendedProperties(state).withProperty(PropConnections, (0..3).map(this::mapConnection))
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    if (capability == Capabilities.Connectable) return connectable as T
    return super.getCapability(capability, side)
  }

  override val validEdges: Set<EnumFaceLocation>
    get() = validSides[facing]!!.map { EnumFaceLocation.fromFaces(it, facing) }.toSet()

  override val rayCollisionBox: AxisAlignedBB?
    get() = baseBounds.rotate(facing)

  override fun getPartSlot(): IPartSlot = EnumFaceSlot.fromFace(facing)
  override fun rotateBlock(axis: EnumFacing): Boolean = false
  override val properties: Set<IProperty<*>> = super.properties + PropFacing
  override val unlistedProperties: Set<IUnlistedProperty<*>> = super.unlistedProperties + PropConnections
  override val collisionBox: Collection<AxisAlignedBB> = emptySet()
  override val isFullBlock: Boolean = false
  override val hardness: Float = 0.25F
  override val material: Material = Material.IRON

  companion object {
    val PropFacing: PropertyEnum<EnumFacing> = PropertyEnum.create("facing", EnumFacing::class.java)
    val PropConnections = object : IUnlistedProperty<List<EnumWireConnection>> {
      @Suppress("UNCHECKED_CAST")
      override fun getType(): Class<List<EnumWireConnection>> = List::class.java as Class<List<EnumWireConnection>> // really kotlin?

      override fun getName(): String = "connections"
      override fun valueToString(value: List<EnumWireConnection>): String = value.toString()
      override fun isValid(value: List<EnumWireConnection>?): Boolean = value != null
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

    // Used to determine outline bounding boxes. (why does this have to be so stupid)

    val lookupMap2 = mapOf(
      DOWN to listOf(EAST, SOUTH, WEST, NORTH),
      UP to listOf(EAST, SOUTH, WEST, NORTH),
      NORTH to listOf(EAST, UP, WEST, DOWN),
      SOUTH to listOf(WEST, UP, EAST, DOWN),
      WEST to listOf(NORTH, UP, SOUTH, DOWN),
      EAST to listOf(SOUTH, UP, NORTH, DOWN)
    )
  }
}