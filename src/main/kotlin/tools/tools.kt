package therealfarfetchd.quacklib.tools

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation

fun getResourceFromName(name: String): ResourceLocation {
  val domain: String
  val iname: String

  if (name.contains(":")) {
    val (d, n) = name.split(":", limit = 2)
    domain = d
    iname = n
  } else {
    domain = ModContext.currentMod()?.modId ?: "minecraft"
    iname = name
  }

  return ResourceLocation(domain, iname)
}

fun TileEntity.copy(): TileEntity? {
  return TileEntity.create(world, NBTTagCompound().let(this::writeToNBT))
}