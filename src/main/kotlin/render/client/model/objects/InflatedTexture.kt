package therealfarfetchd.quacklib.render.client.model.objects

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec2i
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.Vec3i
import therealfarfetchd.quacklib.api.core.extensions.runIf
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.core.modinterface.openResource
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.mkQuad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO
import kotlin.math.absoluteValue

/**
 * A 3d model created from a flat texture. Used e.g. for most normal item models.
 */
class InflatedTexture(val tex: AtlasTexture) : Iterable<Quad> {

  val quads: List<Quad> = computeQuadsForTexture(tex)

  override fun iterator(): Iterator<Quad> = quads.iterator()

}

private fun computeQuadsForTexture(tex: AtlasTexture): List<Quad> {
  val list = mutableListOf<Quad>()

  // front + back
  list += TexturedBox(Vec3(0f, 0f, 7.5f / 16f), Vec3(1f, 1f, 8.5f / 16f), null, null, tex, tex, null, null)

  val image = openResource(fixRes(tex.sourceTexture), true)?.let {
    try {
      ImageIO.read(it)
    } catch (e: IOException) {
      logException(e)
      null
    }
  }

  // sides
  for (f in QuadFacing.values()) {
    list += if (image == null) fallbackDescend(tex, f) else descend(tex, f, image)
  }

  return list
}

fun fixRes(rl: ResourceLocation) =
  ResourceLocation(rl.namespace, "textures/${rl.path}.png")

// Generates quads for the specified facing.
private fun descend(tex: AtlasTexture, f: QuadFacing, image: BufferedImage): List<Quad> {
  val l = mutableListOf<Quad>()

  val right = (tex.size * f.right.xy).length.absoluteValue.toInt()
  val down = (tex.size * f.down.xy).length.absoluteValue.toInt()

  // Remaps coordinates so that x points right and y points down according to the facing.
  val transformCoords = run {
    val max = tex.size - Vec2i(1, 1)
    val offset = f.offset.xy * max
    val trX = f.right.xy
    val trY = f.down.xy

    fun(v: Vec2i): Vec2i {
      return (offset + trX * v.x + trY * v.y) * Vec2i(1, -1) + Vec2i(0, max.y)
    }
  }

  var prevColors = List(right) { 0 }

  for (i in 0 until down) {
    val colors = (0 until right).map { transformCoords(Vec2i(it, i)) }.map { image.getRGB(it.x, it.y) }

    val map = (0 until right).map { compareColors(prevColors[it], colors[it]) }
    l += createQuads(f, i, tex, map)

    prevColors = colors
  }

  return l
}

// Determine if a new part should be inserted by comparing the previous and new colors.
// Currently only handles alpha values.
private fun compareColors(prev: Int, new: Int): Boolean {
  val prevAlpha = prev ushr 24
  val newAlpha = new ushr 24

  if (prevAlpha < 255 && newAlpha == 255) return true
  if (prevAlpha == 0 && newAlpha > 0) return true

  return false
}

// just put quads everywhere, idc
private fun fallbackDescend(tex: AtlasTexture, f: QuadFacing): List<Quad> {
  val l = mutableListOf<Quad>()
  val right = (tex.size * f.right.xy).length.absoluteValue.toInt()
  val down = (tex.size * f.down.xy).length.absoluteValue.toInt()
  val a = List(right) { true }
  for (i in 0 until down) {
    l += createQuads(f, i, tex, a)
  }
  return l
}

// Creates quads from the parts list at the specified depth. Multiple parts will be merged into one quad if possible
private fun createQuads(f: QuadFacing, depth: Int, tex: AtlasTexture, parts: List<Boolean>): List<Quad> {
  val list = mutableListOf<Quad>()

  val chunks: List<IntRange> = run {
    val l = mutableListOf<IntRange>()
    var min: Int? = null
    for ((i, p) in (parts + false).withIndex()) {
      if (p && min == null) {
        min = i
      } else if (!p && min != null) {
        l += min until i
        min = null
      }
    }

    l
  }

  val dPart = (tex.size * f.down.xy).length.absoluteValue
  val rPart = (tex.size * f.right.xy).length.absoluteValue

  for (c in chunks) {
    val min = (f.down * depth.toFloat() / dPart) + Vec3(0f, 0f, 7.5f / 16) + (f.right * c.start / rPart) + f.offset
    val max = (f.down * depth.toFloat() / dPart) + Vec3(0f, 0f, 8.5f / 16) + (f.right * (c.endInclusive + 1) / rPart) + f.offset
    val minuv = Vec2i(0, 1) + Vec2i(1, -1) * ((f.down.xy * depth.toFloat() / dPart) + (f.right.xy * c.start / rPart) + f.offset.xy)
    val maxuv = Vec2i(0, 1) + Vec2i(1, -1) * ((f.down.xy * (depth.toFloat() + 1) / dPart) + (f.right.xy * (c.endInclusive + 1) / rPart) + f.offset.xy)
    list += mkQuad(tex, f.facing, min, max, minuv, maxuv).runIf(f.flip) { flipTexturedSide }
  }

  return list
}

private val Vec3i.xy get() = Vec2i(x, y)

// Which side of the texture we're looking at.
private enum class QuadFacing(val facing: EnumFacing, val down: Vec3i, val right: Vec3i, val offset: Vec3i) {
  XN(EnumFacing.WEST, Vec3i(1, 0, 0), Vec3i(0, -1, 0), Vec3i(0, 1, 0)),
  XP(EnumFacing.EAST, Vec3i(-1, 0, 0), Vec3i(0, -1, 0), Vec3i(1, 1, 0)),
  YN(EnumFacing.DOWN, Vec3i(0, 1, 0), Vec3i(1, 0, 0), Vec3i(0, 0, 0)),
  YP(EnumFacing.UP, Vec3i(0, -1, 0), Vec3i(1, 0, 0), Vec3i(0, 1, 0));

  val flip = (facing.axisDirection == EnumFacing.AxisDirection.NEGATIVE) != (facing.axis == EnumFacing.Axis.X)
}