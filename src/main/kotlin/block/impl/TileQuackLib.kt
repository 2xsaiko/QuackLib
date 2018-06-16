package therealfarfetchd.quacklib.block.impl

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponentCapability
import therealfarfetchd.quacklib.api.block.component.BlockComponentData
import therealfarfetchd.quacklib.api.block.component.BlockComponentTickable
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.block.data.*

open class TileQuackLib() : TileEntity() {

  @Suppress("LeakingThis")
  constructor(def: BlockConfiguration) : this() {
    setConfiguration(def)
  }

  lateinit var def: BlockConfiguration

  var components: List<BlockComponent> = emptyList()

  var cCapability: List<BlockComponentCapability> = emptyList()
  var cPart: List<BlockComponentData<BlockDataPart>> = emptyList()

  var parts: Map<ResourceLocation, BlockDataPart> = emptyMap()

  open fun setConfiguration(def: BlockConfiguration) {
    this.def = def
    components = def.components

    cCapability = getComponentsOfType()
    cPart = getComponentsOfType()

    parts = emptyMap()

    cPart.forEach {
      val rl = it.rl
      val part = it.createPart()
      part.setStorage(StorageImpl(part))
      parts += rl to part
    }
  }

  override fun readFromNBT(nbt: NBTTagCompound) {
    super.readFromNBT(nbt)
    if (!nbt.hasKey("@type")) return
    val type = ResourceLocation(nbt.getString("@type"))
    if (!::def.isInitialized || type != def.rl) {
      if (!ForgeRegistries.BLOCKS.containsKey(type)) return
      val block = ForgeRegistries.BLOCKS.getValue(type) as? BlockQuackLib ?: return
      setConfiguration(block.def)
    }
    for (c in cPart) {
      val partNBT = nbt.getCompoundTag(c.rl.toString())
      val version = partNBT.getInteger("@version")
      val new = c.createPart()
      val latest = new.version
      val part = if (version == latest) new else c.createPart(version)
      part.setStorage(StorageImpl(part))
      new.setStorage(StorageImpl(new))

      val defs = part.defs.filterValues { it.persistent }
      for ((name, p) in defs) {
        val load = DataPartSerializationRegistryImpl.load(partNBT, p.type, name)
        val r = load.let { if (it == null) p.default else it.value }

        if (p.isValid(r)) part.storage.set(name, r)
      }

      if (version != latest) c.update(version, part, new)
      parts += c.rl to new
    }
  }

  override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
    super.writeToNBT(nbt)
    if (!::def.isInitialized) return nbt

    nbt.setString("@type", def.rl.toString())
    for (c in cPart) {
      val partNBT = NBTTagCompound()
      val part = c.createPart()
      partNBT.setInteger("@version", part.version)
      val defs = part.defs.filterValues { it.persistent }
      val storage = parts.getValue(c.rl).storage
      for ((name, _) in defs) {
        val v = storage.get(name)
        DataPartSerializationRegistryImpl.save(partNBT, name, v)
      }
      if (!partNBT.hasNoTags()) nbt.setTag(c.rl.toString(), partNBT)
    }
    return nbt
  }

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    cCapability.any { it.hasCapability(getBlockData(), capability, facing) } ||
    super.hasCapability(capability, facing)

  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    cCapability.firstOrNull { it.hasCapability(getBlockData(), capability, facing) }?.getCapability(getBlockData(), capability, facing)
    ?: super.getCapability(capability, facing)

  protected fun getBlockData() = BlockDataImpl(world, pos, world.getBlockState(pos))

  protected inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

  class Tickable() : TileQuackLib(), ITickable {

    constructor(def: BlockConfiguration) : this() {
      setConfiguration(def)
    }

    var cTickable: List<BlockComponentTickable> = emptyList()

    override fun setConfiguration(def: BlockConfiguration) {
      super.setConfiguration(def)
      cTickable = getComponentsOfType()
    }

    override fun update() {
      cTickable.forEach { it.onTick(getBlockData()) }
    }

  }

}