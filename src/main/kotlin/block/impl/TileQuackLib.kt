package therealfarfetchd.quacklib.block.impl

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.api.block.component.BlockComponentCapability
import therealfarfetchd.quacklib.api.block.component.BlockComponentCustomClientData
import therealfarfetchd.quacklib.api.block.component.BlockComponentTickable
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.core.QuackLib
import therealfarfetchd.quacklib.objects.block.BlockImpl

open class TileQuackLib() : TileEntity() {

  val c = DataContainer()

  var cCapability = c.getComponentsOfType<BlockComponentCapability>()

  var cCustomUpd = c.getComponentsOfType<BlockComponentCustomClientData>()

  @Suppress("LeakingThis")
  constructor(type: BlockType) : this() {
    setConfiguration(type)
  }

  fun setConfiguration(type: BlockType) {
    c.setType(type)
    updateComponents()
  }

  open fun updateComponents() {
    cCapability = c.getComponentsOfType()
    cCustomUpd = c.getComponentsOfType()
  }

  override fun readFromNBT(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)
    c.loadData(nbt) { _, prop -> prop.persistent }
    updateComponents()
  }

  override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
    super.writeToNBT(nbt)
    c.saveData(nbt) { _, prop -> prop.persistent }
    return nbt
  }

  override fun getUpdateTag(): NBTTagCompound {
    val nbt = super.getUpdateTag()
    c.saveData(nbt) { _, prop -> /*prop.render ||*/ prop.sync }
    cCustomUpd.forEach { it.writeClientData(getBlockData(), nbt, true) }
    return nbt
  }

  override fun handleUpdateTag(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)
    c.loadData(nbt) { _, prop -> /*prop.render ||*/ prop.sync }
    cCustomUpd.forEach { it.readClientData(getBlockData(), nbt, true) }
    updateComponents()
  }

  override fun getUpdatePacket(): SPacketUpdateTileEntity {
    // TODO only sync changed stuff
    var renderUpdate = 0
    val nbt = NBTTagCompound()
    c.saveData(nbt) { _, prop ->
      // if (prop.render) renderUpdate = 1
      /*prop.render ||*/ prop.sync
    }
    cCustomUpd.forEach { it.writeClientData(getBlockData(), nbt, false) }
    return SPacketUpdateTileEntity(getPos(), renderUpdate, nbt)
  }

  override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
    c.loadData(pkt.nbtCompound) { _, prop -> /*prop.render ||*/ prop.sync }
    cCustomUpd.forEach { it.readClientData(getBlockData(), pkt.nbtCompound, false) }
    if (pkt.tileEntityType == 1) {
      getWorld().markBlockRangeForRenderUpdate(getPos(), getPos())
    }
  }

  override fun hasFastRenderer(): Boolean = QuackLib.proxy.canRenderFast(c.type)

  override fun getRenderBoundingBox(): AxisAlignedBB =
    c.type.model.getMaxDimensions().offset(pos)

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    cCapability.any { it.hasCapability(getBlockData(), capability, facing) } ||
    super.hasCapability(capability, facing)

  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    cCapability.firstOrNull { it.hasCapability(getBlockData(), capability, facing) }?.getCapability(getBlockData(), capability, facing)
    ?: super.getCapability(capability, facing)

  protected fun getBlockData() = BlockImpl.createExistingFromTile(this)

  class Tickable() : TileQuackLib(), ITickable {

    constructor(type: BlockType) : this() {
      setConfiguration(type)
    }

    var cTickable: List<BlockComponentTickable> = emptyList()

    override fun updateComponents() {
      super.updateComponents()
      cTickable = c.getComponentsOfType()
    }

    override fun update() {
      cTickable.forEach { it.onTick(getBlockData()) }
    }

  }

  override fun toString(): String {
    return "Tile '${c.type.registryName}' (${c.type.components.size} components)"
  }

}