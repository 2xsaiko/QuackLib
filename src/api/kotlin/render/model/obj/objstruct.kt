package therealfarfetchd.quacklib.api.render.model.obj

import therealfarfetchd.math.Vec3
import java.awt.Color

data class OBJRoot(
  val materials: Map<String, Material>,

  val vertPos: List<Vec3>,
  val vertTex: List<Vec3>,

  // these don't belong to any object
  val faces: List<Face>,
  val objects: Map<String, Object>
)

data class Object(
  val groups: List<String>,
  val faces: List<Face>
)

data class Face(
  val material: String?,
  val vertices: List<Vertex>
)

data class Vertex(
  val xyz: Int,
  val tex: Int?
)

data class Material(
  val diffuse: Color,
  val transparency: Float,
  val diffuseTexture: String?
  // for now
)

fun OBJRoot.triangulate(): OBJRoot {
  fun triangulateFace(f: Face) =
    (2 until f.vertices.size).map { Face(f.material, listOf(f.vertices.first(), f.vertices[it - 1], f.vertices[it])) }

  return copy(
    faces = faces.flatMap(::triangulateFace),
    objects = objects.mapValues { (_, o) -> o.copy(faces = o.faces.flatMap(::triangulateFace)) }
  )
}

//fun OBJRoot.quadulate(): OBJRoot {
//  fun processFaces(f: List<Face>): List<Face> {
//    val candidates = f.filter { it.vertices.size == 3 }.toMutableList()
//    val r = f.toMutableList()
//    do {
//      var processed = false
//      for (tri in candidates.toList()) {
//        if (tri.vertices.size != 3) continue
//
//        // criteria for two triangles to merge:
//        // 2 verts must be equal
//        // normal must be equal
//        // material must be equal
//
//        val normal = run {
//          val (v1, v2, v3) = tri.vertices.map { vertPos[getRealIndex(vertPos.size, it.xyz)] }
//          ((v2 - v1) crossProduct (v3 - v1)).normalized
//        }
//
//        val tri2 = candidates.asSequence().filter {
//          it !== tri &&
//          it.material == tri.material &&
//          it.vertices.size == 3 &&
//          it.vertices.count { it in tri.vertices } == 2 && run {
//            // ((vert2 - vert1) crossProduct (vert3 - vert1)).normalized
//            val (v1, v2, v3) = it.vertices.map { vertPos[getRealIndex(vertPos.size, it.xyz)] }
//            (((v2 - v1) crossProduct (v3 - v1)).normalized) == normal
//          }
//        }.firstOrNull()
//
//        if (tri2 != null) {
//          val inserted = (tri2.vertices - tri.vertices).single()
//          val dupIndices = tri.vertices.withIndex().filter { (_, v) -> v in tri2.vertices }.map { it.index }.sorted().zipWithNext().single()
//
//          val insertPos = when (dupIndices) {
//            Pair(0, 2) -> 0
//            Pair(0, 1) -> 1
//            Pair(1, 2) -> 2
//            else -> error("this shouldn't happen. and if it does, here's debug info: $inserted $dupIndices $tri $tri2")
//          }
//
//          val (r1, r2) = tri.vertices.withIndex().partition { (i, _) -> i < insertPos }
//          val quad = tri.copy(vertices = r1.map { it.value } + inserted + r2.map { it.value })
//
//          val i = r.indexOf(tri)
//          r.remove(tri)
//          r.remove(tri2)
//          r.add(i, quad)
//
//          candidates.remove(tri)
//          candidates.remove(tri2)
//
//          processed = true
//          break
//        }
//      }
//    } while (processed)
//    return r
//  }
//
//  var result = this
//
//  result = result.copy(faces = processFaces(result.faces), objects = objects.mapValues { (_, v) -> v.copy(faces = processFaces(v.faces)) })
//
//  return result
//}
//
//private fun getRealIndex(total: Int, a: Int) = when {
//  a > 0 -> a - 1
//  a < 0 -> total + a
//  else -> error("Invalid index!")
//}