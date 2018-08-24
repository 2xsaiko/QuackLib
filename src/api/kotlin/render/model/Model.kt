package therealfarfetchd.quacklib.api.render.model

import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture
import kotlin.reflect.KClass

interface Model {

  fun <T : DataSource<*>> accepts(type: KClass<T>): Boolean

  fun getCacheStrategy(): CacheStrategy = CacheStrategy.Mixed

  fun <T : DataSource<*>> getStaticRender(data: T, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad>

  fun <T : DataSource<D>, D : DynDataSource> getDynamicRender(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture): List<Quad> = emptyList()

  fun <T : DataSource<D>, D : DynDataSource> renderGl(data: T, dyndata: D, getTexture: (ResourceLocation) -> AtlasTexture) {}

  fun getUsedTextures(): List<ResourceLocation>

  // TODO: ugh
  fun isItemTransformation(): Boolean = false

  fun getMaxDimensions(): AxisAlignedBB = Block.FULL_BLOCK_AABB

  fun getParticleTexture(getTexture: (ResourceLocation) -> AtlasTexture): AtlasTexture

  fun needsDynamicRender(): Boolean = false

  fun needsGlRender(): Boolean = false

}