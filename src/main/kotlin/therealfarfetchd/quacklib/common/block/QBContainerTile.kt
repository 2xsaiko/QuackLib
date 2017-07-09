package therealfarfetchd.quacklib.common.block

import net.minecraft.block.Block
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.common.DataTarget

/**
 * Created by marco on 08.07.17.
 */
open class QBContainerTile() : TileEntity() {

  private var _qb: QBlock? = null
  var qb: QBlock
    get() = _qb!!
    set(value) {
      check(_qb == null || _qb == value) { "nope" }
      _qb = value
      value.container = this
    }

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

  override fun writeToNBT(compound: NBTTagCompound): NBTTagCompound {
    val subTag = NBTTagCompound()
    compound.setString("BlockType", getBlockType().registryName.toString())
    qb.saveData(subTag, DataTarget.Save)
    compound.setTag("QBlockData", subTag)
    return super.writeToNBT(compound)
  }

  override fun readFromNBT(compound: NBTTagCompound) {
    super.readFromNBT(compound)
    if (_qb == null) {
      val rl = ResourceLocation(compound.getString("BlockType"))
      val block = Block.REGISTRY.getObject(rl)
      if (block is QBContainer) qb = block.factory()
      else throw IllegalStateException("Block is not a QBContainer! (got block $rl ($block) which is ${block::class}")
    }
    val subTag = compound.getCompoundTag("QBlockData")
    qb.loadData(subTag, DataTarget.Save)
  }

  override fun getUpdatePacket(): SPacketUpdateTileEntity? = SPacketUpdateTileEntity(pos, 0, updateTag)

  override fun getUpdateTag(): NBTTagCompound {
    val tag = super.getUpdateTag()
    val subTag = NBTTagCompound()
    qb.saveData(subTag, DataTarget.Client)
    tag.setTag("D", subTag)
    tag.setBoolean("R", nextClientUpdateIsRender)
    nextClientUpdateIsRender = false
    return tag
  }

  override fun onDataPacket(net: NetworkManager?, pkt: SPacketUpdateTileEntity) = handleUpdateTag(pkt.nbtCompound)

  override fun handleUpdateTag(tag: NBTTagCompound) {
    super.readFromNBT(tag)
    qb.loadData(tag.getCompoundTag("D"), DataTarget.Client)
    if (tag.getBoolean("R")) {
      world.markBlockRangeForRenderUpdate(pos, pos)
    }
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
    return super.hasCapability(capability, facing) || qb.getCapability(capability, facing) != null
  }

  override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
    return qb.getCapability(capability, facing) ?: super.getCapability(capability, facing)
  }

  open class Ticking() : QBContainerTile(), ITickable {
    constructor(qbIn: QBlock) : this() {
      QBContainerTile(qbIn)
    }

    private val tickable get() = qb as ITickable

    override fun update() = tickable.update()
  }

}