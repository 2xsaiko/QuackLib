package therealfarfetchd.quacklib.render.model.objloader

import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.core.QuackLib.Logger
import java.io.IOException

internal fun getRelativeResource(base: ResourceLocation, rpath: String): ResourceLocation {
  return when {
    ':' in rpath -> ResourceLocation(rpath)
    rpath.startsWith('/') -> ResourceLocation(base.namespace, rpath.trimStart('/'))
    else -> ResourceLocation(base.namespace, base.path.dropLastWhile { it != '/' } + rpath)
  }
}

internal fun readCustom(s: String, ignore: Int, m: Int, o: Int): List<String> {
  val components = s.split(" ").drop(ignore)
  if ((o >= 0 && components.size !in m..m + o) || components.size < m)
    error("Invalid number of parameters! Expected $m mandatory, $o optional, got ${components.size}")
  return components
}

internal fun readFloats(s: String, ignore: Int, m: Int, o: Int): List<Float> = readCustom(s, ignore, m, o).map(String::toFloat)

internal fun readResource(rl: ResourceLocation): String? {
  val resourceManager = Minecraft.getMinecraft().resourceManager
  val resource = try {
    resourceManager.getResource(rl)
  } catch (e: IOException) {
    Logger.error("OBJ file not found: $rl")
    return null
  }

  return resource.inputStream.use { it.bufferedReader().readText() }
}