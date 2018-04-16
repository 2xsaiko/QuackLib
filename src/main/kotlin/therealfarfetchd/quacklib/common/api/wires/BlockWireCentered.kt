package therealfarfetchd.quacklib.common.api.wires

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.EnumCenterSlot
import mcmultipart.api.slot.IPartSlot
import mcmultipart.block.TileMultipartContainer
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.common.api.block.capability.Capabilities
import therealfarfetchd.quacklib.common.api.block.capability.CenteredWireConnectable
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.EnumFacingExtended
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

abstract class BlockWireCentered<out T>(val width: Double) : QBlock(), IQBlockMultipart, TileConnectable {
  @Suppress("LeakingThis")
  protected var cr = ConnectionResolverTile(this)

  val baseBounds = FullAABB.grow(width / 2 - 1)

  @Suppress("LeakingThis")
  private val connectable =
    EnumFacing.VALUES.map { it to CenteredWireConnectable(this, it) }.toMap()

  abstract val dataType: ResourceLocation

  abstract val data: T

  open fun getAdditionalData(side: EnumFacing, facing: EnumFacing?, key: String): Any? = null

  override fun connectionsChanged() {
    super.connectionsChanged()
    clientDataChanged()
  }

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    cr.updateCableConnections()
  }

  override fun onNeighborChanged(side: EnumFacing) {
    super.onNeighborChanged(side)
    cr.updateCableConnections()
  }

  override fun onNeighborTEChanged(side: EnumFacing) {
    super.onNeighborTEChanged(side)
    cr.updateCableConnections()
  }

  override fun onPartChanged(part: IPartInfo) {
    super.onPartChanged(part)
    cr.updateCableConnections()
  }

  override fun collideParts(mp: TileMultipartContainer, wc: EnumWireConnection, e: EnumFacingExtended): Boolean {
    // TODO
    return false
  }

  override fun getBlockFaceShape(facing: EnumFacing): BlockFaceShape = BlockFaceShape.UNDEFINED

  override fun canHarvestBlock(player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Boolean = true

  override fun blockStrength(player: EntityPlayer): Float = 1f / hardness / 30f

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    if (!prePlaced) nbt.bytes["C"] = cr.serializeConnections().toByteArray()
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    if (!prePlaced) cr.deserializeConnections(nbt.bytes["C"].toList())
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    return when (capability) {
      Capabilities.Connectable   -> side?.let { connectable[it] as T }
      TileConnectable.Capability -> this as T
      else                       -> super.getCapability(capability, side)
    }
  }

  override fun applyProperties(state: IBlockState): IBlockState {
    return super.applyProperties(state)
      .withProperty(PropConnUp, cr.connections[EnumFacingExtended.CenterUp] != null)
      .withProperty(PropConnDown, cr.connections[EnumFacingExtended.CenterDown] != null)
      .withProperty(PropConnNorth, cr.connections[EnumFacingExtended.CenterNorth] != null)
      .withProperty(PropConnSouth, cr.connections[EnumFacingExtended.CenterSouth] != null)
      .withProperty(PropConnWest, cr.connections[EnumFacingExtended.CenterWest] != null)
      .withProperty(PropConnEast, cr.connections[EnumFacingExtended.CenterEast] != null)
  }

  override fun getConnectionResolver() = cr

  override fun getTile() = container

  override fun getWorldForScan() = actualWorld

  override fun getPartSlot(): IPartSlot = EnumCenterSlot.CENTER

  override val properties: Set<IProperty<*>> =
    super.properties + PropConnUp + PropConnDown + PropConnNorth + PropConnSouth + PropConnWest + PropConnEast

  override val collisionBox: Collection<AxisAlignedBB> = setOf(baseBounds)
  override val isFullBlock: Boolean = false
  override val hardness: Float = 0.25F
  override val material: Material = Material.IRON

  companion object {
    val PropConnUp: PropertyBool = PropertyBool.create("up")
    val PropConnDown: PropertyBool = PropertyBool.create("down")
    val PropConnNorth: PropertyBool = PropertyBool.create("north")
    val PropConnSouth: PropertyBool = PropertyBool.create("south")
    val PropConnWest: PropertyBool = PropertyBool.create("west")
    val PropConnEast: PropertyBool = PropertyBool.create("east")
  }
}