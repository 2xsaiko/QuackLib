package therealfarfetchd.quacklib.common.api.util.math

//import net.minecraft.util.math.AxisAlignedBB
//import therealfarfetchd.quacklib.common.api.extensions.mapWithCopy
//import therealfarfetchd.quacklib.common.api.qblock.QBlock
//import kotlin.concurrent.thread
//
//class AdvancedBoundingBox(
//  private val hitCondition: (Vec3) -> Boolean,
//  private val maxBounds: AxisAlignedBB = QBlock.FullAABB,
//  startResolution: Float = maxBounds.averageEdgeLength.toFloat() / 2f,
//  private val resolution: Float = maxBounds.averageEdgeLength.toFloat() / 16f
//) {
//  private var _boxes: Set<AxisAlignedBB> = setOf(maxBounds)
//
//  private var generated = false
//
//  private var currentResolution = startResolution
//
//  //  private fun createAABoxes() = runBlocking {
//  //    val fulloffset = Vec3(resolution, resolution, resolution)
//  //    val halfoffset = fulloffset / 2f
//  //    buildSequence {
//  //      var current = Vec3(maxBounds.minX, maxBounds.minY, maxBounds.minZ)
//  //      while (current.z < maxBounds.maxZ) {
//  //        yield(current)
//  //        current =
//  //          current.copy(x = current.x + resolution).takeIf { it.x < maxBounds.maxX } ?:
//  //          current.copy(y = current.y + resolution, x = maxBounds.minX.toFloat()).takeIf { it.y < maxBounds.maxY } ?:
//  //          current.copy(z = current.z + resolution, y = maxBounds.minY.toFloat(), x = maxBounds.minX.toFloat())
//  //      }
//  //    }
//  //      .filter { hitCondition(it + halfoffset) }
//  //      .map { AxisAlignedBB(it.toVec3d(), (it + fulloffset).toVec3d()) }
//  //    //      .mapWithCopy { AxisAlignedBB(it.toVec3d(), (it + fulloffset).toVec3d()) }
//  //    //      .mapFirst { Triple((it.x / resolution).toInt(), (it.y / resolution).toInt(), (it.z / resolution).toInt()) }
//  //
//  //  }
//
//  private fun createAABoxes(): Collection<AxisAlignedBB> {
//    val halfoffset = currentResolution / 2f
//
//    var im: Map<Set<Triple<Int, Int, Int>>, AxisAlignedBB> = emptyMap()
//
//    for (i in maxBounds.minX..maxBounds.maxX step currentResolution.toDouble()) {
//      val ci = convx(i)
//      var jm: Map<Set<Pair<Int, Int>>, AxisAlignedBB> = emptyMap()
//
//      for (j in maxBounds.minY..maxBounds.maxY step currentResolution.toDouble()) {
//        val cj = convy(j)
//        var km: Map<Set<Int>, AxisAlignedBB> = emptyMap()
//
//        for (k in maxBounds.minZ..maxBounds.maxZ step currentResolution.toDouble()) {
//          val ck = convz(k)
//          if (hitCondition(Vec3(i + halfoffset, j + halfoffset, k + halfoffset))) {
//            val aabb = AxisAlignedBB(i, j, k, i + currentResolution, j + currentResolution, k + currentResolution)
//
//            val kprev: Set<Int>? = km.keys.firstOrNull { ck - 1 in it }
//            if (kprev != null) {
//              km[kprev]?.also {
//                km = km.filterKeys { it != kprev }
//                km += (kprev + ck) to it.union(aabb)
//              }
//            } else {
//              km += setOf(ck) to aabb
//            }
//          }
//        }
//
//        km.forEach { (ke, aabb) ->
//          val jprev = jm.keys.firstOrNull { it == ke.mapWithCopy { cj - 1 }.toSet() }
//          val ck = ke.map { Pair(it, cj - 1) }.toSet()
//          if (jprev != null) {
//            jm[jprev]?.also {
//              jm = jm.filterKeys { it != jprev }
//              jm += (jprev + ck) to it.union(aabb)
//            }
//          } else {
//            jm += ck to aabb
//          }
//        }
//      }
//      jm.forEach { (ke, aabb) ->
//        val iprev = im.keys.firstOrNull { it == ke.mapWithCopy { ci - 1 }.toSet() }
//        val ck = ke.map { Triple(it.first, it.second, ci - 1) }.toSet()
//        if (iprev != null) {
//          im[iprev]?.also {
//            im = im.filterKeys { it != iprev }
//            im += (iprev + ck) to it.union(aabb)
//          }
//        } else {
//          im += ck to aabb
//        }
//      }
//    }
//
//    return im.map { it.value }
//  }
//
//  private fun convx(d: Double): Int = ((d - maxBounds.minX) / currentResolution).toInt()
//  private fun convy(d: Double): Int = ((d - maxBounds.minY) / currentResolution).toInt()
//  private fun convz(d: Double): Int = ((d - maxBounds.minZ) / currentResolution).toInt()
//
//  fun getBoxes(): Set<AxisAlignedBB> {
//    if (!generated) {
//      generated = true
//      thread {
//        while (currentResolution >= resolution) {
//          _boxes = createAABoxes().toSet()
//          currentResolution /= 2f
//        }
//      }
//    }
//    return _boxes
//  }
//
//  companion object {
//    fun fromMatrix(mat: Mat4, resolution: Float = 1 / 16f): AdvancedBoundingBox {
//      val max = sequenceOf(
//        Vec3(0f, 0f, 0f),
//        Vec3(1f, 0f, 0f),
//        Vec3(1f, 0f, 1f),
//        Vec3(0f, 0f, 1f),
//        Vec3(0f, 1f, 0f),
//        Vec3(1f, 1f, 0f),
//        Vec3(1f, 1f, 1f),
//        Vec3(0f, 1f, 1f)
//      )
//        .map { (mat * it).toVec3d() }
//        .map { AxisAlignedBB(it, it) }
//        .reduce(AxisAlignedBB::union)
//
//      println(max)
//
//      println(mat.inverse * Vec3(2f, 2f, 2f))
//
//      return AdvancedBoundingBox({
//        val v = mat.inverse * it
//        v.x in 0f..1f &&
//        v.y in 0f..1f &&
//        v.z in 0f..1f
//      }, max, minOf(resolution, resolution * max.averageEdgeLength.toFloat()))
//    }
//  }
//}
//
//fun AxisAlignedBB.isSameXY(other: AxisAlignedBB) =
//  minX == other.minX && maxX == other.maxX &&
//  minY == other.minY && maxY == other.maxY
//
//fun AxisAlignedBB.isSameXZ(other: AxisAlignedBB) =
//  minX == other.minX && maxX == other.maxX &&
//  minZ == other.minZ && maxZ == other.maxZ
//
//fun AxisAlignedBB.isSameYZ(other: AxisAlignedBB) =
//  minY == other.minY && maxY == other.maxY &&
//  minZ == other.minZ && maxZ == other.maxZ
//
//private fun <T> Collection<T>.random(): T = toList().let { it[Random.nextInt(it.size)] }