package therealfarfetchd.quacklib.client.api.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.extensions.RGBA
import therealfarfetchd.quacklib.common.api.util.math.Vec2
import therealfarfetchd.quacklib.common.api.util.math.Vec3

object QuadFactory {
  @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
  fun makeQuad(x: Double, y: Double, z: Double, x1: Double, y1: Double, z1: Double, facing: EnumFacing, u: Double, v: Double, u1: Double, v1: Double, texture: TextureAtlasSprite): Quad {
    val vec2 = when (facing.axis) {
      EnumFacing.Axis.X -> Vec3((x + x1) / 2, y1, z)
      EnumFacing.Axis.Y -> Vec3(x, (y + y1) / 2, z1)
      EnumFacing.Axis.Z -> Vec3(x, y1, (z + z1) / 2)
    }

    val vec4 = when (facing.axis) {
      EnumFacing.Axis.X -> Vec3((x + x1) / 2, y, z1)
      EnumFacing.Axis.Y -> Vec3(x1, (y + y1) / 2, z)
      EnumFacing.Axis.Z -> Vec3(x1, y, (z + z1) / 2)
    }

    val fv = facing.directionVec

    return Quad(texture, Vec3(x, y, z), vec2, Vec3(x1, y1, z1), vec4, Vec2(u, v), Vec2(u, v1), Vec2(u1, v1), Vec2(u1, v), RGBA(1f, 1f, 1f, 1f))
  }

  fun makeQuad(x: Double, y: Double, z: Double, x1: Double, y1: Double, z1: Double, facing: EnumFacing, u: Double, v: Double, u1: Double, v1: Double, texture: ResourceLocation): Quad {
    val textureMapBlocks = Minecraft.getMinecraft().textureMapBlocks
    var sprite = textureMapBlocks.getTextureExtry(texture.toString())
    if (sprite == null) sprite = textureMapBlocks.missingSprite!!
    return QuadFactory.makeQuad(x, y, z, x1, y1, z1, facing, u, v, u1, v1, sprite)
  }

  fun makeQuad_16(x: Double, y: Double, z: Double, x1: Double, y1: Double, z1: Double, facing: EnumFacing, u: Double, v: Double, u1: Double, v1: Double, texture: TextureAtlasSprite): Quad {
    return QuadFactory.makeQuad(x / 16f, y / 16f, z / 16f, x1 / 16f, y1 / 16f, z1 / 16f, facing, u, v, u1, v1, texture)
  }

  fun makeQuad_16(x: Double, y: Double, z: Double, x1: Double, y1: Double, z1: Double, facing: EnumFacing, u: Double, v: Double, u1: Double, v1: Double, texture: ResourceLocation): Quad {
    return makeQuad(x / 16f, y / 16f, z / 16f, x1 / 16f, y1 / 16f, z1 / 16f, facing, u, v, u1, v1, texture)
  }

  fun makeQuadw(x: Double, y: Double, z: Double, width: Double, height: Double, depth: Double, facing: EnumFacing, uv: Vec2, twidth: Double, theight: Double, texture: TextureAtlasSprite, scaleFactor: Double): Quad {
    return QuadFactory.makeQuad(x, y, z, x + width, y + height, z + depth, facing, uv.x, uv.y, uv.x + twidth / scaleFactor, uv.y + theight / scaleFactor, texture)
  }
}