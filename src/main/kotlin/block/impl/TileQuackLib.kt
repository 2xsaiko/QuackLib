package therealfarfetchd.quacklib.block.impl

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.api.block.component.BlockComponentCapability
import therealfarfetchd.quacklib.api.block.component.BlockComponentTickable
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.block.data.BlockDataImpl

open class TileQuackLib() : TileEntity() {

  val c = DataContainer()

  var cCapability = c.getComponentsOfType<BlockComponentCapability>()

  @Suppress("LeakingThis")
  constructor(def: BlockConfiguration) : this() {
    setConfiguration(def)
  }

  open fun setConfiguration(def: BlockConfiguration) {
    c.setConfiguration(def)

    cCapability = c.getComponentsOfType()
  }

  override fun readFromNBT(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)
    c.loadData(nbt) { _, prop -> prop.persistent }
  }

  override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
    super.writeToNBT(nbt)
    c.saveData(nbt) { _, prop -> prop.persistent }
    return nbt
  }

  override fun getUpdateTag(): NBTTagCompound {
    val nbt = super.getUpdateTag()
    c.saveData(nbt) { _, prop -> prop.render || prop.sync }
    return nbt
  }

  override fun handleUpdateTag(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)
    c.loadData(nbt) { _, prop -> prop.render || prop.sync }
  }

  override fun getUpdatePacket(): SPacketUpdateTileEntity {
    // TODO only sync changed stuff
    var renderUpdate = 0
    val nbt = NBTTagCompound()
    c.saveData(nbt) { _, prop ->
      if (prop.render) renderUpdate = 1
      prop.render || prop.sync
    }
    return SPacketUpdateTileEntity(getPos(), renderUpdate, nbt)
  }

  override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
    c.loadData(pkt.nbtCompound) { _, prop -> prop.render || prop.sync }
    if (pkt.tileEntityType == 1) {
      getWorld().markBlockRangeForRenderUpdate(getPos(), getPos())
    }
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    cCapability.any { it.hasCapability(getBlockData(), capability, facing) } ||
    super.hasCapability(capability, facing)

  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    cCapability.firstOrNull { it.hasCapability(getBlockData(), capability, facing) }?.getCapability(getBlockData(), capability, facing)
    ?: super.getCapability(capability, facing)

  protected fun getBlockData() = BlockDataImpl(world, pos, world.getBlockState(pos))

  class Tickable() : TileQuackLib(), ITickable {

    constructor(def: BlockConfiguration) : this() {
      setConfiguration(def)
    }

    var cTickable: List<BlockComponentTickable> = emptyList()

    override fun setConfiguration(def: BlockConfiguration) {
      super.setConfiguration(def)
      cTickable = c.getComponentsOfType()
    }

    override fun update() {
      cTickable.forEach { it.onTick(getBlockData()) }
    }

  }

}