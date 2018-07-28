package therealfarfetchd.quacklib.block.impl

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponentData
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.tools.Logger
import therealfarfetchd.quacklib.block.data.DataPartSerializationRegistryImpl
import therealfarfetchd.quacklib.block.data.StorageImpl
import therealfarfetchd.quacklib.block.data.get
import therealfarfetchd.quacklib.block.data.set

class DataContainer {

  lateinit var type: BlockType
    @JvmName("setType0") private set

  var components: List<BlockComponent> = emptyList()

  var cPart: List<BlockComponentData<BlockDataPart>> = emptyList()

  var parts: Map<ResourceLocation, BlockDataPart> = emptyMap()

  fun setType(type: BlockType) {
    this.type = type
    components = type.components

    cPart = getComponentsOfType()

    parts = emptyMap()

    cPart.forEach {
      val rl = it.rl
      val part = it.createPart()
      part.setStorage(StorageImpl(part))
      parts += rl to part
    }
  }

  fun import(other: DataContainer) {
    if (other.type != type) setType(other.type)
    parts = other.parts
  }

  fun saveData(nbt: NBTTagCompound, filter: (BlockComponentData<BlockDataPart>, BlockDataPart.ValueProperties<Any?>) -> Boolean) {
    if (!::type.isInitialized) return

    nbt.setString("@type", type.registryName.toString())
    for (c in cPart) {
      try {
        val partNBT = NBTTagCompound()
        val part = c.createPart()
        partNBT.setInteger("@version", part.version)
        val defs = part.defs.filterValues { filter(c, it) }
        val storage = parts.getValue(c.rl).storage
        for ((name, _) in defs) {
          val v = storage.get(name)
          DataPartSerializationRegistryImpl.save(partNBT, name, v)
        }
        if (!partNBT.isEmpty) nbt.setTag(c.rl.toString(), partNBT)
      } catch (e: Exception) {
        Logger.error("Could not serialize component ${c.rl}!")
        logException(e)
      }
    }
  }

  fun loadData(nbt: NBTTagCompound, filter: (BlockComponentData<BlockDataPart>, BlockDataPart.ValueProperties<Any?>) -> Boolean) {
    if (!nbt.hasKey("@type")) return
    val typePath = ResourceLocation(nbt.getString("@type"))
    if (!::type.isInitialized || typePath != type.registryName) {
      if (!ForgeRegistries.BLOCKS.containsKey(typePath)) return
      val block = ForgeRegistries.BLOCKS.getValue(typePath) as? BlockQuackLib ?: return
      setType(block.type)
    }
    for (c in cPart) {
      try {
        val partNBT = nbt.getCompoundTag(c.rl.toString())
        val version = partNBT.getInteger("@version")
        val part = c.createPart(version)
        val storage = StorageImpl(part)
        val processed = mutableSetOf<String>()
        part.setStorage(storage)

        val defs = part.defs.filterValues { filter(c, it) }
        for ((name, p) in defs) {
          val load = DataPartSerializationRegistryImpl.load(partNBT, p.type, name)
          val r = load.let {
            if (it == null) {
              p.default
            } else {
              processed += name
              it.value
            }
          }

          if (p.isValid(r)) storage.set(name, r)
        }

        val new = updatePart(c, part)
        parts[c.rl]?.let { merge(new, updatePart(c, it), processed) }
        parts += c.rl to new
      } catch (e: Exception) {
        Logger.error("Could not deserialize component ${c.rl}!")
        logException(e)
      }
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun <T : BlockDataPart> updatePart(c: BlockComponentData<T>, old: BlockDataPart): T {
    val new = c.createPart()
    if (new.version == old.version) return old as T
    new.setStorage(StorageImpl(new))
    c.update(old, new)
    return new
  }

  private fun <T : BlockDataPart> merge(into: T, from: T, ignore: Set<String>) {
    (from.defs.keys - ignore).forEach { into.storage.set(it, from.storage.get(it)) }
  }

  inline fun <reified T : BlockComponent> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}