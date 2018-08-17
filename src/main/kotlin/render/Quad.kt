package therealfarfetchd.quacklib.render

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraftforge.client.model.pipeline.IVertexConsumer
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.alphaf
import therealfarfetchd.quacklib.api.core.extensions.bluef
import therealfarfetchd.quacklib.api.core.extensions.greenf
import therealfarfetchd.quacklib.api.core.extensions.redf
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.QuadBase
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl

fun Quad.bake(format: VertexFormat = DefaultVertexFormats.ITEM) = bake({ it }, format)

fun Quad.pipe(c: IVertexConsumer) = pipe(c) { it }

fun <T> QuadBase<T>.bake(mapper: (T) -> AtlasTexture, format: VertexFormat = DefaultVertexFormats.ITEM): BakedQuad {
  val builder = UnpackedBakedQuad.Builder(format)
  pipe(builder, mapper)
  return builder.build()
}

fun <T> QuadBase<T>.pipe(c: IVertexConsumer, mapper: (T) -> AtlasTexture) {
  val rt = mapper(texture)
  val vertices = (0..3).map { i ->
    val (xyz, uv) = shuf(i)
    xyz to rt.interpolate(uv)
  }

  c.setApplyDiffuseLighting(true)
  c.setQuadOrientation(facing)

  c.setQuadTint(-1)
  c.setTexture((texture as AtlasTextureImpl).tas)

  for ((xyz, uv) in vertices) {
    for ((i, el) in c.vertexFormat.elements.withIndex()) {
      when (el.usage) {
        VertexFormatElement.EnumUsage.POSITION -> c.put(i, xyz.x, xyz.y, xyz.z, 1f)
        VertexFormatElement.EnumUsage.NORMAL -> c.put(i, normal.x, normal.y, normal.z, 0f)
        VertexFormatElement.EnumUsage.COLOR -> c.put(i, color.redf, color.greenf, color.bluef, color.alphaf)
        VertexFormatElement.EnumUsage.UV -> when (el.index) {
          0 -> c.put(i, uv.x, uv.y, 0f, 1f) // texture
          1 -> c.put(i, lightmap.x, lightmap.y, 0f, 1f) // lightmap
        }
        else -> c.put(i)
      }
    }
  }
}

private fun QuadBase<*>.shuf(i: Int): Pair<Vec3, Vec2> {
  return when (i) {
    0 -> vert1 to tex1
    1 -> vert2 to tex2
    2 -> vert3 to tex3
    3 -> vert4 to tex4
    else -> error("$i must be in 0..3!")
  }
}