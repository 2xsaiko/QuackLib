package therealfarfetchd.quacklib.api.core.extensions

import net.minecraft.client.renderer.BufferBuilder

fun BufferBuilder.pos(x: Float, y: Float, z: Float): BufferBuilder =
  pos(x.toDouble(), y.toDouble(), z.toDouble())

fun BufferBuilder.tex(u: Float, v: Float): BufferBuilder =
  tex(u.toDouble(), v.toDouble())