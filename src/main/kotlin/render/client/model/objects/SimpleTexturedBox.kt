package therealfarfetchd.quacklib.render.client.model.objects

import net.minecraft.util.EnumFacing.*
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.mkQuad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

class SimpleTexturedBox(from: Vec3, to: Vec3, val tex: AtlasTexture, rotate: Boolean = false) : Iterable<Quad> {

  @Suppress("UnnecessaryVariable")
  val quads = run {
    // val p1 = from
    val p2 = Vec3(from.x, from.y, to.z)
    // val p3 = Vec3(to.x, from.y, to.z)
    val p4 = Vec3(to.x, from.y, from.z)
    val p5 = Vec3(from.x, to.y, from.z)
    // val p6 = Vec3(from.x, to.y, to.z)
    val p7 = to
    // val p8 = Vec3(to.x, to.y, from.z)

    listOf(
      mkQuad(tex, DOWN, p7, p5, rotate = rotate),
      mkQuad(tex, UP, p2, p4, rotate = rotate),
      mkQuad(tex, NORTH, p7, p2, rotate = rotate),
      mkQuad(tex, SOUTH, p4, p5, rotate = rotate),
      mkQuad(tex, WEST, p4, p7, rotate = rotate),
      mkQuad(tex, EAST, p2, p5, rotate = rotate)
    )
  }

  override fun iterator(): Iterator<Quad> = quads.iterator()

}