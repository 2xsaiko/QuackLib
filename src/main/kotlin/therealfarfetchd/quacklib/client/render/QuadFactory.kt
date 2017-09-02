package therealfarfetchd.quacklib.client.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.util.Vec2
import therealfarfetchd.quacklib.common.util.Vec3

object QuadFactory {
  @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
  fun makeQuad(x: Float, y: Float, z: Float, x1: Float, y1: Float, z1: Float, facing: EnumFacing, u: Float, v: Float, u1: Float, v1: Float, texture: TextureAtlasSprite): Quad {
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

    return Quad(texture, Vec3(fv.x.toFloat(), fv.y.toFloat(), fv.z.toFloat()), Vec3(x, y, z), vec2, Vec3(x1, y1, z1), vec4, Vec2(u, v), Vec2(u1, v1), false)
  }

  fun makeQuad(x: Float, y: Float, z: Float, x1: Float, y1: Float, z1: Float, facing: EnumFacing, u: Float, v: Float, u1: Float, v1: Float, texture: ResourceLocation): Quad {
    val textureMapBlocks = Minecraft.getMinecraft().textureMapBlocks
    var sprite = textureMapBlocks.getTextureExtry(texture.toString())
    if (sprite == null) sprite = textureMapBlocks.missingSprite!!
    return makeQuad(x, y, z, x1, y1, z1, facing, u, v, u1, v1, sprite)
  }

  fun makeQuad_16(x: Float, y: Float, z: Float, x1: Float, y1: Float, z1: Float, facing: EnumFacing, u: Float, v: Float, u1: Float, v1: Float, texture: TextureAtlasSprite): Quad {
    return makeQuad(x / 16f, y / 16f, z / 16f, x1 / 16f, y1 / 16f, z1 / 16f, facing, u / 16f, v / 16f, u1 / 16f, v1 / 16f, texture)
  }

  fun makeQuad_16(x: Float, y: Float, z: Float, x1: Float, y1: Float, z1: Float, facing: EnumFacing, u: Float, v: Float, u1: Float, v1: Float, texture: ResourceLocation): Quad {
    return makeQuad(x / 16f, y / 16f, z / 16f, x1 / 16f, y1 / 16f, z1 / 16f, facing, u / 16f, v / 16f, u1 / 16f, v1 / 16f, texture)
  }
}