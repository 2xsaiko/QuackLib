package therealfarfetchd.quacklib.render.model.objloader

import therealfarfetchd.quacklib.api.render.QuadBase

data class OBJQuads<T>(
  val quads: List<QuadBase<T>>,
  val objects: Map<String, ObjectQ<T>>
)

data class ObjectQ<T>(
  val groups: List<String>,
  val quads: List<QuadBase<T>>
)

sealed class Texture {
  object Empty : Texture()
  data class Named(val name: String) : Texture()
}