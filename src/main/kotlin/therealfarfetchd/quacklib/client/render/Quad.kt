package therealfarfetchd.quacklib.client.render

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import therealfarfetchd.quacklib.common.util.Vec2
import therealfarfetchd.quacklib.common.util.Vec3

data class Quad(val texture: TextureAtlasSprite, val facingVec: Vec3, val vert1: Vec3, val vert2: Vec3, val vert3: Vec3, val vert4: Vec3, val tex1: Vec2, val tex2: Vec2, private val rotTex: Boolean) {
  val facing: EnumFacing by lazy { EnumFacing.getFacingFromVector(facingVec.x, facingVec.y, facingVec.z) }

  fun bake(): BakedQuad {
    val vertices = (0..3).map { i ->
      val (xyz, uv) = shuf(i)
      xyz to Vec2(texture.getInterpolatedU(uv.x * 16.0), texture.getInterpolatedV(uv.y * 16.0))
    }

    val diff1 = vertices[1].first - vertices[0].first
    val diff2 = vertices[2].first - vertices[0].first
    val normal = (diff1 crossProduct diff2).normalize().copy()

    val builder = UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM)
    builder.setApplyDiffuseLighting(true)
    builder.setQuadOrientation(facing)
    builder.setQuadTint(-1)
    builder.setTexture(texture)

    for ((xyz, uv) in vertices) {
      builder.put(0, xyz.x, xyz.y, xyz.z, 1f)
      builder.put(1, 1f, 1f, 1f, 1f)
      builder.put(2, uv.x, uv.y, 0f, 1f)
      builder.put(3, normal.x, normal.y, normal.z, 0f)
      builder.put(4)
    }

    return builder.build()
  }

  private fun shuf(i: Int): Pair<Vec3, Vec2> {
    if (rotTex) {
      return when (i) {
        0 -> vert1 to Vec2(tex1.x, tex2.y)
        1 -> vert2 to tex2
        2 -> vert3 to Vec2(tex2.x, tex1.y)
        3 -> vert4 to tex1
        else -> error("$i must be in 0..3!")
      }
    } else {
      return when (i) {
        0 -> vert1 to tex1
        1 -> vert2 to Vec2(tex1.x, tex2.y)
        2 -> vert3 to tex2
        3 -> vert4 to Vec2(tex2.x, tex1.y)
        else -> error("$i must be in 0..3!")
      }
    }
  }

  /**
   * Translates (offsets) this quad by the given vector
   *
   * @return The moved quad
   */

  fun translate(vec: Vec3): Quad {
    return Quad(texture, facingVec, vert1 + vec, vert2 + vec, vert3 + vec, vert4 + vec, tex1, tex2, rotTex)
  }

  /**
   * Rotates the quad around the given axis with the angle a
   *
   * @param axis The axis to rotate around
   * @param a    The angle (0..360)
   * @return The rotated quad
   */

  fun rotate(axis: EnumFacing.Axis, a: Float): Quad {
    return if (a == 0.0F) this.copy()
    else {
      val r = listOf(vert1, vert2, vert3, vert4, facingVec).map { it.rotate(a, axis, Vec3(0.5f, 0.5f, 0.5f)) }
      Quad(texture, r[4], r[0], r[1], r[2], r[3], tex1, tex2, rotTex)
    }
  }

  /**
   * Rotates the texture by 90° (or reverts the rotation).
   * Use this if you have a texture that is aligned horizontally, but the quad needs it to be vertical and vice-versa.
   *
   * @return The quad with a rotated texture
   */

  val rotatedTexture: Quad by lazy { copy(rotTex = !rotTex) }

  /**
   * Flips the textured side of this quad.
   *
   * @return The quad with the flipped texture
   */

  val flipTexturedSide: Quad by lazy { copy(facingVec = Vec3(-facingVec.x, -facingVec.y, -facingVec.z), vert1 = vert2, vert2 = vert1, vert3 = vert4, vert4 = vert3, tex1 = Vec2(tex1.x, tex2.y), tex2 = Vec2(tex2.x, tex1.y)) }

  fun mirror(axis: EnumFacing.Axis): Quad {
    return (when (axis) {
      EnumFacing.Axis.X -> copy(
        vert1 = Vec3(1 - vert1.x, vert1.y, vert1.z),
        vert2 = Vec3(1 - vert2.x, vert2.y, vert2.z),
        vert3 = Vec3(1 - vert3.x, vert3.y, vert3.z),
        vert4 = Vec3(1 - vert4.x, vert4.y, vert4.z)
      )
      EnumFacing.Axis.Y -> copy(
        vert1 = Vec3(vert1.x, 1 - vert1.y, vert1.z),
        vert2 = Vec3(vert2.x, 1 - vert2.y, vert2.z),
        vert3 = Vec3(vert3.x, 1 - vert3.y, vert3.z),
        vert4 = Vec3(vert4.x, 1 - vert4.y, vert4.z)
      )
      EnumFacing.Axis.Z -> copy(
        vert1 = Vec3(vert1.x, vert1.y, 1 - vert1.z),
        vert2 = Vec3(vert2.x, vert2.y, 1 - vert2.z),
        vert3 = Vec3(vert3.x, vert3.y, 1 - vert3.z),
        vert4 = Vec3(vert4.x, vert4.y, 1 - vert4.z)
      )
    }).flipTexturedSide
  }

}