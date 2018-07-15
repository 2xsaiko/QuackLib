package therealfarfetchd.quacklib.api.render

import net.minecraft.util.EnumFacing.*
import net.minecraft.util.EnumFacing.Axis.*
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.runIf
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.tools.Facing
import java.awt.Color

fun mkQuad(tex: AtlasTexture, facing: Facing, from: Vec3, to: Vec3, uv: Vec2, uv1: Vec2, c: Color = Color.WHITE): Quad {
  val vec2 = when (facing.axis) {
    X -> from.copy(x = (from.x + to.x) / 2, y = to.y)
    Y -> from.copy(y = (from.y + to.y) / 2, z = to.z)
    Z -> from.copy(z = (from.z + to.z) / 2, x = to.x)
  }

  val vec4 = when (facing.axis) {
    X -> to.copy(x = (from.x + to.x) / 2, y = from.y)
    Y -> to.copy(y = (from.y + to.y) / 2, z = from.z)
    Z -> to.copy(z = (from.z + to.z) / 2, x = from.x)
  }

  return Quad(tex, from, vec2, to, vec4, uv, uv.copy(y = uv1.y), uv1, uv1.copy(y = uv.y), c)
}

fun mkQuad16(tex: AtlasTexture, facing: Facing, from: Vec3, to: Vec3, uv: Vec2, uv1: Vec2, c: Color = Color.WHITE): Quad {
  return mkQuad(tex, facing, from / 16, to / 16, uv, uv1, c)
}

fun mkQuad(tex: AtlasTexture, facing: Facing, from: Vec3, to: Vec3, c: Color = Color.WHITE, rotate: Boolean = false): Quad {
  val (uv1, uv2) = getuvfromxyz(facing, from, to)

  return postprocQuad(facing, mkQuad(tex, facing, from, to, uv1, uv2, c), rotate)
}

fun mkQuad16(tex: AtlasTexture, facing: Facing, from: Vec3, to: Vec3, c: Color = Color.WHITE, rotate: Boolean = false): Quad {
  val (uv1, uv2) = getuvfromxyz(facing, from, to)

  return postprocQuad(facing, mkQuad16(tex, facing, from, to, uv1, uv2, c), rotate)
}

private fun getuvfromxyz(facing: Facing, from: Vec3, to: Vec3): Pair<Vec2, Vec2> {
  val uv1 = when (facing.axis) {
    X -> Vec2(from.z, from.y)
    Y -> Vec2(from.x, from.z)
    Z -> Vec2(from.x, from.y)
  }

  val uv2 = when (facing.axis) {
    X -> Vec2(to.z, to.y)
    Y -> Vec2(to.x, to.z)
    Z -> Vec2(to.x, to.y)
  }

  return Pair(uv1, uv2)
}

private fun postprocQuad(f: Facing, q: Quad, rotate: Boolean): Quad = when (f) {
  DOWN -> q
  UP -> q
  NORTH -> q.rotatedTexture90.runIf(rotate) { mirrorTextureX }
  SOUTH -> q.rotatedTexture90.runIf(rotate) { mirrorTextureX.mirrorTextureY }
  WEST -> q.runIf(rotate) { mirrorTextureX }
  EAST -> q
}