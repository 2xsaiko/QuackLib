package therealfarfetchd.quacklib.common.api.qblock

import com.elytradev.mirage.lighting.IColoredLight
import com.elytradev.mirage.lighting.Light
import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.Optional
import therealfarfetchd.quacklib.common.api.extensions.copyTo
import therealfarfetchd.quacklib.common.api.extensions.packByte
import therealfarfetchd.quacklib.common.api.extensions.unpack
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

/**
 * Created by marco on 08.07.17.
 */
@Optional.Interface(iface = "com.elytradev.mirage.lighting.IColoredLight", modid = "mirage")
open class QBContainerTile() : TileEntity(), IColoredLight {
  private var _qb: QBlock? = null

  var qb: QBlock
    get() = _qb!!
    internal set(value) {
      _qb = value
      value.container = this
    }
  /**
   * 0: qb.onAdded() called
   * 1-6: unused
   */
  protected val bits: BooleanArray = BooleanArray(8)

  var nextClientUpdateIsRender: Boolean = false

  constructor(qbIn: QBlock) : this() {
    qb = qbIn
  }

  override fun setWorld(world: World) {
    super.setWorld(world)
    qb.world = world
  }

  override fun setPos(pos: BlockPos) {
    super.setPos(pos)
    qb.pos = pos
  }

  @Optional.Method(modid = "mirage")
  override fun getColoredLight(): Light? {
    return (qb as? IQBlockColoredLight)?.getColoredLight()
  }

  override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
    val subTag = NBTTagCompound()
    compound.setByte("B", packByte(*bits))
    compound.setString("T", getBlockType().registryName.toString())
    qb.saveData(QNBTCompound(subTag), DataTarget.Save)
    compound.setTag("D", subTag)
    return super.writeToNBT(compound)
  }

  override fun readFromNBT(compound: NBTTagCompound) {
    super.readFromNBT(compound)

    // backward compat
    if (compound.hasKey("Bits")) compound.setTag("B", compound.getTag("Bits"))
    if (compound.hasKey("BlockType")) compound.setTag("T", compound.getTag("BlockType"))
    if (compound.hasKey("QBlockData")) compound.setTag("B", compound.getTag("QBlockData"))

    unpack(compound.getByte("B")).copyTo(bits)
    if (_qb == null) {
      val rl = ResourceLocation(compound.getString("T"))
      val block = Block.REGISTRY.getObject(rl)
      if (block is QBContainer) qb = block.factory()
      else throw IllegalStateException("Block is not a QBContainer! (got block $rl ($block) which is ${block::class}")
    }
    val subTag = compound.getCompoundTag("D")
    qb.pos = getPos()
    qb.loadData(QNBTCompound(subTag), DataTarget.Save)
  }

  override fun validate() {
    qb.validate()
    super.validate()
  }

  override fun getUpdatePacket(): SPacketUpdateTileEntity? = SPacketUpdateTileEntity(pos, 0, updateTag)

  override fun getUpdateTag(): NBTTagCompound {
    val tag = super.getUpdateTag()
    val subTag = NBTTagCompound()
    qb.saveData(QNBTCompound(subTag), DataTarget.Client)
    tag.setTag("D", subTag)
    tag.setBoolean("R", nextClientUpdateIsRender)
    nextClientUpdateIsRender = false
    return tag
  }

  override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity) = handleUpdateTag(pkt.nbtCompound)

  override fun handleUpdateTag(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    qb.loadData(QNBTCompound(tag.getCompoundTag("D")), DataTarget.Client)
    if (tag.getBoolean("R")) {
      world.markBlockRangeForRenderUpdate(pos, pos)
    }
  }

  override fun onLoad() {
    super.onLoad()
    if (!bits[0]) {
      bits[0] = true
      qb.onAdded()
    }
    qb.onLoad()
  }

  override fun onChunkUnload() {
    super.onChunkUnload()
    qb.onUnload()
  }

  override fun hasFastRenderer(): Boolean {
    return qb.hasFastRenderer
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
    return super.hasCapability(capability, facing) || qb.getCapability(capability, facing) != null
  }

  override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
    return qb.getCapability(capability, facing) ?: super.getCapability(capability, facing)
  }

  open class Ticking() : QBContainerTile(), ITickingQBTile {
    constructor(qbIn: QBlock) : this() {
      qb = qbIn
    }
  }

}