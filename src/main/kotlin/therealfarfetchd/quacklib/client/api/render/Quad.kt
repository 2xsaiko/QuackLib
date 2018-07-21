package therealfarfetchd.quacklib.client.api.render

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraftforge.client.model.pipeline.IVertexConsumer
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import therealfarfetchd.quacklib.common.api.extensions.RGBA
import therealfarfetchd.quacklib.common.api.util.math.Mat4
import therealfarfetchd.quacklib.common.api.util.math.Vec2
import therealfarfetchd.quacklib.common.api.util.math.Vec3
import therealfarfetchd.quacklib.common.api.util.math.times

data class Quad(
  val texture: TextureAtlasSprite,
  val vert1: Vec3, val vert2: Vec3, val vert3: Vec3, val vert4: Vec3,
  val tex1: Vec2, val tex2: Vec2, val tex3: Vec2, val tex4: Vec2,
  val color: RGBA
) {
  val normal by lazy { ((vert2 - vert1) crossProduct (vert3 - vert1)).normalize() }

  val facing: EnumFacing by lazy { EnumFacing.getFacingFromVector(normal.xf, normal.yf, normal.zf) }

  fun bake(format: VertexFormat = DefaultVertexFormats.ITEM): BakedQuad {
    val builder = UnpackedBakedQuad.Builder(format)
    pipe(builder)
    return builder.build()
  }

  fun pipe(c: IVertexConsumer) {
    val vertices = (0..3).map { i ->
      val (xyz, uv) = shuf(i)
      xyz to Vec2(texture.getInterpolatedU(uv.xf * 16.0).toDouble(), texture.getInterpolatedV(uv.yf * 16.0).toDouble())
    }

    c.setApplyDiffuseLighting(true)
    c.setQuadOrientation(facing)

    c.setQuadTint(-1)
    c.setTexture(texture)

    for ((xyz, uv) in vertices) {
      for ((i, el) in c.vertexFormat.elements.withIndex()) {
        when (el.usage) {
          VertexFormatElement.EnumUsage.POSITION -> c.put(i, xyz.xf, xyz.yf, xyz.zf, 1f)
          VertexFormatElement.EnumUsage.NORMAL -> c.put(i, normal.xf, normal.yf, normal.zf, 0f)
          VertexFormatElement.EnumUsage.COLOR -> c.put(i, color.first, color.second, color.third, color.fourth)
          VertexFormatElement.EnumUsage.UV -> when (el.index) {
            0 -> c.put(i, uv.xf, uv.yf, 0f, 1f) // texture
            1 -> c.put(i, 0f, 0f, 0f, 1f) // lightmap
          }
          else -> c.put(i)
        }
      }
    }
  }

  private fun ftoi(float: Float) = (float * 255).toInt()

  private fun shuf(i: Int): Pair<Vec3, Vec2> {
    return when (i) {
      0 -> vert1 to tex1
      1 -> vert2 to tex2
      2 -> vert3 to tex3
      3 -> vert4 to tex4
      else -> error("$i must be in 0..3!")
    }
  }

  /**
   * Translates (offsets) this quad by the given vector
   *
   * @return The moved quad
   */

  fun translate(vec: Vec3) =
    transform(Mat4.translateMat(vec.x, vec.y, vec.z))

  /**
   * Rotates the quad around the given axis with the angle a
   *
   * @param axis The axis to rotate around
   * @param a    The angle (0..360)
   * @return The rotated quad
   */

  fun rotate(axis: EnumFacing.Axis, a: Double, center: Vec3 = Vec3(0.5, 0.5, 0.5)): Quad {
    return if (a == 0.0) this.copy()
    else {
      val r = listOf(vert1, vert2, vert3, vert4).map { it.rotate(a, axis, center) }
      Quad(texture, r[0], r[1], r[2], r[3], tex1, tex2, tex3, tex4, color)
    }
  }

  fun transform(mat: Mat4) =
    copy(vert1 = mat * vert1, vert2 = mat * vert2, vert3 = mat * vert3, vert4 = mat * vert4)

  /**
   * Rotates the texture by 90°.
   * Use this if you have a texture that is aligned horizontally, but the quad needs it to be vertical and vice-versa.
   *
   * @return The quad with a rotated texture
   */

  val rotatedTexture90: Quad by lazy { copy(tex1 = tex2, tex2 = tex3, tex3 = tex4, tex4 = tex1) }

  /**
   * Rotates a texture by 180°.
   */
  val rotatedTexture180: Quad by lazy { copy(tex1 = tex3, tex2 = tex4, tex3 = tex1, tex4 = tex2) }

  /**
   * Flips the texture on the x axis.
   */
  val mirrorTextureX: Quad by lazy { copy(tex1 = tex4, tex2 = tex3, tex3 = tex2, tex4 = tex1) }

  /**
   * Flips the texture on the y axis.
   */
  val mirrorTextureY: Quad by lazy { copy(tex1 = tex2, tex2 = tex1, tex3 = tex4, tex4 = tex3) }

  /**
   * Flips the textured side of this quad.
   *
   * @return The quad with the flipped texture
   */

  val flipTexturedSide: Quad by lazy { copy(vert1 = vert2, vert2 = vert1, vert3 = vert4, vert4 = vert3).mirrorTextureY }

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