package therealfarfetchd.quacklib.tools

import net.minecraft.util.ResourceLocation

fun getResourceFromName(name: String): ResourceLocation {
  val domain: String
  val iname: String

  if (name.contains(":")) {
    val (d, n) = name.split(":")
    domain = d
    iname = n
  } else {
    domain = ModContext.currentMod()?.modId ?: "minecraft"
    iname = name
  }

  return ResourceLocation(domain, iname)
}