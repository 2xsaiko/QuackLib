package therealfarfetchd.quacklib.render.model.objloader

import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.resource.IResourceType
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.modinterface.logException
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.QuadBase
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.render.client.texGetter
import java.awt.Color
import java.util.function.Predicate

private val empty = ResourceLocation(ModID, "white")
private val unset = ResourceLocation(ModID, "pablo")

object OBJModelProvider : ISelectiveResourceReloadListener {
  private val cache = mutableMapOf<ResourceLocation, OBJRoot?>()
  private val qcache = mutableMapOf<ResourceLocation, OBJQuads<Texture>?>()
  private val tcache = mutableMapOf<Pair<ResourceLocation, Map<String, SimpleModel.PreparedTexture?>>, OBJQuads<AtlasTexture>?>()

  fun load(rl: ResourceLocation): OBJRoot? {
    return cache.computeIfAbsent(rl) {
      try {
        loadOBJ(it)
      } catch (e: Exception) {
        logException(e)
        null
      }
    }
  }

  fun loadQuads(rl: ResourceLocation): OBJQuads<Texture>? {
    return qcache.computeIfAbsent(rl) {
      val obj = load(rl) ?: return@computeIfAbsent null

      OBJQuads(
        quads = obj.faces.mapNotNull { faceToQuad(obj, it) },
        objects = obj.objects.mapValues { (_, it) ->
          ObjectQ(
            groups = it.groups,
            quads = it.faces.mapNotNull { faceToQuad(obj, it) }
          )
        }
      )
    }
  }

  fun loadQuadsPrepared(rl: ResourceLocation, textures: Map<String, SimpleModel.PreparedTexture?>): OBJQuads<AtlasTexture>? {
    return tcache.computeIfAbsent(Pair(rl, textures)) {
      val obj = loadQuads(rl) ?: return@computeIfAbsent null

      OBJQuads(
        quads = obj.quads.mapNotNull { mapQuad(it, textures) },
        objects = obj.objects.mapValues { (_, it) ->
          ObjectQ(
            groups = it.groups,
            quads = it.quads.mapNotNull { mapQuad(it, textures) }
          )
        }
      )
    }
  }

  private fun mapQuad(q: QuadBase<Texture>, textures: Map<String, SimpleModel.PreparedTexture?>): Quad? {
    return when (
      val t = q.texture) {
      Texture.Empty -> empty
      is Texture.Named -> {
        if (t.name in textures) textures[t.name]?.resource
        else unset
      }
    }?.let { tex -> q.withTexture(texture = texGetter(tex)) }
  }

  @SideOnly(Side.CLIENT)
  override fun onResourceManagerReload(resourceManager: IResourceManager?, resourcePredicate: Predicate<IResourceType>?) {
    cache.clear()
    qcache.clear()
    tcache.clear()
  }

  private fun faceToQuad(data: OBJRoot, face: Face): QuadBase<Texture>? {
    val verts: List<Vec3> = face.vertices.map { data.vertPos[getRealIndex(data.vertPos.size, it.xyz)] }

    var uvs: List<Vec2>? = run {
      val r = face.vertices.map { it.tex?.let { data.vertTex[getRealIndex(data.vertTex.size, it)] } }
      if (null in r) null else r.filterNotNull()
    }?.map { Vec2(it.x, it.y) }

    val color = data.materials[face.material]?.diffuse ?: Color.WHITE

    val tex: Texture? =
      if (uvs == null) Texture.Empty
      else when (face.material) {
        null -> Texture.Empty
        else -> Texture.Named(face.material)
      }

    if (uvs == null) uvs = verts.map { Vec2(0.5f, 0.5f) }

    uvs = uvs.map { it.copy(y = 1 - it.y) } // v is upside down in OBJ

    if (tex == null) return null

    return when (verts.size) {
      3 -> {
        QuadBase(
          tex,
          verts[0], verts[1], verts[2], verts[2],
          uvs[0], uvs[1], uvs[2], uvs[2],
          Vec2.Origin, color
        )
      }
      4 -> {
        QuadBase(
          tex,
          verts[0], verts[1], verts[2], verts[3],
          uvs[0], uvs[1], uvs[2], uvs[3],
          Vec2.Origin, color
        )
      }
      else -> error("Eh? Got face with ${verts.size} vertices!?")
    }
  }

  private fun getRealIndex(total: Int, a: Int) = when {
    a > 0 -> a - 1
    a < 0 -> total + a
    else -> error("Invalid index!")
  }

}