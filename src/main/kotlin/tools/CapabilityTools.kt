package therealfarfetchd.quacklib.tools

import net.minecraft.nbt.NBTBase
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.fml.common.discovery.ASMDataTable
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
fun <T : Any> registerCapability(type: KClass<T>) {
  CapabilityManager.INSTANCE.register<T>(
    type.java,
    OmniStorage as Capability.IStorage<T>,
    OmniFactory
  )
}

fun registerAnnotatedCapabilities(table: ASMDataTable) {
  for (c in table.getAll("therealfarfetchd.quacklib.api.tools.RegisterCapability").map { it.className }.distinct()) {
    registerCapability(Class.forName(c).kotlin)
  }
}

private object OmniStorage : Capability.IStorage<Nothing> {

  override fun readNBT(capability: Capability<Nothing>?, instance: Nothing?, side: EnumFacing?, nbt: NBTBase?) {}

  override fun writeNBT(capability: Capability<Nothing>?, instance: Nothing?, side: EnumFacing?) = null

}

private val OmniFactory = { null }