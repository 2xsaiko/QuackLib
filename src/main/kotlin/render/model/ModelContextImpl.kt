package therealfarfetchd.quacklib.render.model

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.math.Mat4
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.getDistance
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.*
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.api.render.texture.Texture
import kotlin.math.max
import kotlin.math.min

open class ModelContextImpl(
  override val data: DataSource<*>,
  val getTexture: (ResourceLocation) -> AtlasTexture,
  val allowDyn: Boolean,
  val allowGl: Boolean
) : SimpleModel.ModelContext {

  override val Box: ObjectBuilderProvider<BoxConfigurationScope> = ::BoxConfigurationScopeImpl
  override val OBJ: ObjectBuilderProvider<ObjConfigurationScope> = ::ObjConfigurationScopeImpl

  override var coordsScale = 1f

  override var constraints: AxisAlignedBB? = null

  val quads = mutableListOf<Quad>()

  val transformStack = mutableListOf<Mat4>()
  var currentTransform = Mat4.Identity

  val dynops = mutableListOf<DynState>()
  val glops = mutableListOf<GlState>()

  internal fun getQuads(): List<Quad> {
    return quads
  }

  override fun texture(pt: SimpleModel.PreparedTexture): Texture {
    if (pt.isAtlasTex) {
      return getTexture(pt.resource)
    } else {
      TODO("getting non-atlas textures not implemented yet")
    }
  }

  override fun addQuad(q: Quad) {
    var q1: Quad?
    q1 = q.transform(currentTransform)
    val constraints = constraints
    if (constraints != null) {
      val verts = listOf(q1.vert1, q1.vert2, q1.vert3, q1.vert4)
      val contained = verts.count {
        it.x in constraints.minX..constraints.maxX &&
        it.y in constraints.minY..constraints.maxY &&
        it.z in constraints.minZ..constraints.maxZ
      }

      if (contained == 0) {
        q1 = null
      } else if (contained < 4) {
        val list = listOf(
          Pair(q1.vert1, q1.tex1),
          Pair(q1.vert2, q1.tex2),
          Pair(q1.vert3, q1.tex3),
          Pair(q1.vert4, q1.tex4)
        )

        val v = list.map { (v, tex) ->
          // FIXME: this breaks with quads that are not aligned to the axis, but not going to bother right now
          // this is already enough of a pain as it is

          val r = Vec3(
            min(max(constraints.minX.toFloat(), v.x), constraints.maxX.toFloat()),
            min(max(constraints.minY.toFloat(), v.y), constraints.maxY.toFloat()),
            min(max(constraints.minZ.toFloat(), v.z), constraints.maxZ.toFloat())
          )

          val (v1, tex1) = list.maxBy { getDistance(it.first, v) }!!

          fun x(v: Vec3): Vec2 = when (q1!!.facing) {
            EnumFacing.DOWN, EnumFacing.UP -> Vec2(v.z, v.x)
            EnumFacing.NORTH, EnumFacing.SOUTH -> Vec2(v.x, v.y)
            EnumFacing.WEST, EnumFacing.EAST -> Vec2(v.y, v.z)
          }

          val r1 = x(v)
          val r1n = x(r)
          val r2 = x(v1)
          val r1d = (r1n - r2) / (r1 - r2)

          Pair(r, (tex - tex1) * r1d + tex1)
        }

        q1 = q1.copy(
          vert1 = v[0].first,
          vert2 = v[1].first,
          vert3 = v[2].first,
          vert4 = v[3].first,
          tex1 = v[0].second,
          tex2 = v[1].second,
          tex3 = v[2].second,
          tex4 = v[3].second
        )
      }
    }

    if (q1 != null) quads += q1
  }

  override fun <T : ModelConfigurationScope> add(prov: ObjectBuilderProvider<T>, op: T.() -> Unit) {
    addQuads(prov(this).also(op).getQuads(getTexture))
  }

  override fun trPush() {
    transformStack += currentTransform
  }

  override fun trPop() {
    currentTransform = transformStack.removeAt(transformStack.size - 1)
  }

  override fun rotate(angle: Float, x: Float, y: Float, z: Float) {
    transform(Mat4.rotate(x, y, z, angle))
  }

  override fun translate(x: Float, y: Float, z: Float) {
    transform(Mat4.translate(x / coordsScale, y / coordsScale, z / coordsScale))
  }

  override fun scale(x: Float, y: Float, z: Float) {
    transform(Mat4.scale(x, y, z))
  }

  override fun transform(mat: Mat4) {
    currentTransform *= mat
  }

  override fun coordsScale(f: Float) {
    coordsScale = f
  }

  override fun constraints(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float) {
    constraints = AxisAlignedBB(x1.toDouble(), y1.toDouble(), z1.toDouble(), x2.toDouble(), y2.toDouble(), z2.toDouble())
  }

  override fun dynamic(op: SimpleModel.Dynamic.() -> Unit) {
    if (!allowDyn) {
      // FIXME this should be a validation error
      error("Can't use dynamic objects in this model! Try setting useDynamic to true")
    }
    dynops += DynState(transformStack + currentTransform, coordsScale, op)
  }

  override fun gl(op: SimpleModel.GlContext.() -> Unit) {
    if (!allowGl) {
      // FIXME this should be a validation error
      error("Can't use GL drawing in this model! Try setting useGL to true")
    }
    glops += GlState(op)
  }

  data class DynState(val trStack: List<Mat4>, val cscale: Float, val op: SimpleModel.Dynamic.() -> Unit)

  data class GlState(val op: SimpleModel.GlContext.() -> Unit)

  class DynWrapper(
    data: DataSource<*>,
    getTexture: (ResourceLocation) -> AtlasTexture,
    allowDyn: Boolean,
    allowGl: Boolean) : ModelContextImpl(data, getTexture, allowDyn, allowGl) {

    override fun addQuad(q: Quad) {
      // No-op
    }

    override fun <T : ModelConfigurationScope> add(prov: ObjectBuilderProvider<T>, op: T.() -> Unit) {
      // No-op
    }

  }

  class Dynamic(
    state: DynState,
    data: DataSource<*>,
    override val dyndata: DynDataSource,
    getTexture: (ResourceLocation) -> AtlasTexture
  ) : ModelContextImpl(data, getTexture, true, true), SimpleModel.Dynamic {

    init {
      transformStack += state.trStack.dropLast(1)
      currentTransform = state.trStack.last()
      coordsScale = state.cscale
    }

    override fun dynamic(op: SimpleModel.Dynamic.() -> Unit): Unit =
      error("Can't have nested dynamic blocks!")
  }

  class GlContext(override val data: DataSource<*>, override val dyndata: DynDataSource) : SimpleModel.GlContext

}