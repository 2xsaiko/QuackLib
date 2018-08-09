package therealfarfetchd.quacklib.render.client.model.new

import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.*
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import org.apache.commons.lang3.tuple.Pair
import therealfarfetchd.quacklib.api.core.extensions.toMatrix4f
import therealfarfetchd.quacklib.render.client.ModelCache
import therealfarfetchd.quacklib.render.vanilla.toTransformType
import javax.vecmath.Matrix4f

open class BakedModelCached(val cache: ModelCache, val rl: ModelResourceLocation, val format: VertexFormat) : IBakedModel {

  private val overrides = ItemOverrides()

  override fun getParticleTexture(): TextureAtlasSprite = cache.getParticleTexture(rl)

  override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
    if (state == null) return emptyList()
    return cache.getQuadsBlock(rl, format, state, side, rand)
  }

  override fun isBuiltInRenderer(): Boolean = cache.isBuiltInRenderer(rl)

  override fun isAmbientOcclusion(): Boolean = cache.isAmbientOcclusion(rl)

  override fun isGui3d(): Boolean = cache.isGui3d(rl)

  override fun getOverrides(): ItemOverrideList = overrides

  override fun handlePerspective(cameraTransformType: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> {
    val tr = cache.getTransformation(rl)
    return Pair.of(this, tr.getTransformationMatrix(cameraTransformType.toTransformType())?.toMatrix4f())
  }

  inner class ItemOverrides : ItemOverrideList() {
    override fun handleItemState(originalModel: IBakedModel, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel {
      return BakedModelItemCached(cache, rl, format, stack)
    }
  }

}