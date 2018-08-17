package therealfarfetchd.quacklib.render.model.objloader

import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.core.QuackLib.Logger

fun loadOBJ(rl: ResourceLocation): OBJRoot? {
  val source = readResource(rl) ?: return null

  var materials: Map<String, Material> = emptyMap()
  var vertPos: List<Vec3> = emptyList()
  var vertTex: List<Vec3> = emptyList()
  // var vertNormal: List<Vec3> = emptyList()
  var faces: List<Face> = emptyList()
  var objects: Map<String, Object> = emptyMap()

  var activeMaterial: String? = null

  var objname: String? = null
  var objgroups: List<String> = emptyList()
  var currentFaces: List<Face> = emptyList()

  for (it in source.lines().map { it.replace(regex, "") }.map(String::trim)) {
    if (it.isBlank()) continue
    val cmd = it.split(" ").getOrNull(0)
    when (cmd) {
      "mtllib" -> materials += loadMaterialLibrary(getRelativeResource(rl, readCustom(it, 1, 1, 0)[0])).orEmpty()
      "usemtl" -> activeMaterial = readCustom(it, 1, 1, 0)[0]
      "o" -> {
        if (objname != null) {
          objects += (objname to Object(objgroups, currentFaces))
        }
        objname = readCustom(it, 1, 1, 0)[0]
        objgroups = emptyList()
        currentFaces = emptyList()
      }
      "g" -> {
        if (objname == null) Logger.warn("No active object, ignoring group definition")
        else objgroups += readCustom(it, 1, 1, -1)
      }
      "v" -> {
        val c = readFloats(it, 1, 3, 1)
        vertPos += when (c.size) {
          3 -> Vec3(c[0], c[1], c[2])
          4 -> Vec3(c[0] / c[3], c[1] / c[3], c[2] / c[3])
          else -> error("Invalid position vertex count: ${c.size}")
        }
      }
      "vt" -> {
        val c = readFloats(it, 1, 2, 1)
        vertTex += Vec3(c[0], c[1], c.getOrElse(2) { 0f })
      }
      "vn" -> {
        // val c = readFloats(it, 1, 3, 0)
        // vertNormal += Vec3(c[0], c[1], c[2])
      }
      "f" -> currentFaces += Face(activeMaterial, readCustom(it, 1, 3, -1).map { parseVertex(it) })
      else -> Logger.warn("Unrecognized OBJ statement '$it'")
    }
  }

  if (objname != null) objects += (objname to Object(objgroups, currentFaces))
  else faces += currentFaces

  return OBJRoot(materials, vertPos, vertTex, faces, objects)
}

private fun parseVertex(s: String): Vertex {
  val (c1, c2, _) = s.split("/").map(String::toIntOrNull)
  return Vertex(c1!!, c2)
}

inline fun OBJRoot?.orEmpty(): OBJRoot =
  this ?: OBJRoot(emptyMap(), emptyList(), emptyList(), emptyList(), emptyMap())