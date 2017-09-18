package therealfarfetchd.quacklib.common.api.wires

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.slot.EnumCenterSlot
import mcmultipart.api.slot.IPartSlot
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
import therealfarfetchd.quacklib.common.api.util.EnumFaceLocation
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

abstract class BlockWireCentered<out T>(val width: Double) : QBlock(), IQBlockMultipart, BaseConnectable {

  val baseBounds = FullAABB.grow(width / 2 - 1)

  @Suppress("LeakingThis")
  private val connectable =
    EnumFacing.VALUES.map { it to CenteredWireConnectable(this, it) }.toMap()

  override var connections: Map<EnumFaceLocation, EnumWireConnection> = emptyMap()

  abstract val dataType: ResourceLocation

  abstract val data: T

  open fun getAdditionalData(side: EnumFacing, facing: EnumFacing?, key: String): Any? = null

  override fun updateCableConnections(): Boolean {
    return if (super.updateCableConnections()) {
      clientDataChanged()
      true
    } else false
  }

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    super.onPlaced(placer, stack, sidePlaced, hitX, hitY, hitZ)
    updateCableConnections()
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

  override fun getBlockFaceShape(facing: EnumFacing): BlockFaceShape = BlockFaceShape.UNDEFINED

  override fun canHarvestBlock(player: EntityPlayer, world: IBlockAccess, pos: BlockPos): Boolean = true

  override fun blockStrength(player: EntityPlayer): Float = 1f / hardness / 30f

  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
    super.saveData(nbt, target)
    if (!prePlaced) nbt.bytes["C"] = serializeConnections().toByteArray()
  }

  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
    super.loadData(nbt, target)
    if (!prePlaced) deserializeConnections(nbt.bytes["C"].toList())
  }

  @Suppress("UNCHECKED_CAST")
  override fun <T> getCapability(capability: Capability<T>, side: EnumFacing?): T? {
    if (capability == Capabilities.Connectable && side != null) return connectable[side] as T
    return super.getCapability(capability, side)
  }

  override fun applyProperties(state: IBlockState): IBlockState {
    return super.applyProperties(state)
      .withProperty(PropConnUp, connections[EnumFaceLocation.UpCenter] != null)
      .withProperty(PropConnDown, connections[EnumFaceLocation.DownCenter] != null)
      .withProperty(PropConnNorth, connections[EnumFaceLocation.NorthCenter] != null)
      .withProperty(PropConnSouth, connections[EnumFaceLocation.SouthCenter] != null)
      .withProperty(PropConnWest, connections[EnumFaceLocation.WestCenter] != null)
      .withProperty(PropConnEast, connections[EnumFaceLocation.EastCenter] != null)
  }

  override fun getPartSlot(): IPartSlot = EnumCenterSlot.CENTER

  override val properties: Set<IProperty<*>> =
    super.properties + PropConnUp + PropConnDown + PropConnNorth + PropConnSouth + PropConnWest + PropConnEast

  override val validEdges: Set<EnumFaceLocation> = validFaces
  override val collisionBox: AxisAlignedBB? = baseBounds
  override val isFullBlock: Boolean = false
  override val hardness: Float = 0.25F
  override val material: Material = Material.IRON

  companion object {
    val PropConnUp = PropertyBool.create("up")!!
    val PropConnDown = PropertyBool.create("down")!!
    val PropConnNorth = PropertyBool.create("north")!!
    val PropConnSouth = PropertyBool.create("south")!!
    val PropConnWest = PropertyBool.create("west")!!
    val PropConnEast = PropertyBool.create("east")!!

    val validFaces = EnumFaceLocation.Values.filter { it.side == null }.toSet()
  }

}