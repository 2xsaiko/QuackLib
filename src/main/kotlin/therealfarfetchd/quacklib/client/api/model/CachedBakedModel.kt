package therealfarfetchd.quacklib.client.api.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World

class CachedBakedModel(private val bakery: IModel) : IBakedModel {
  override fun getParticleTexture(): TextureAtlasSprite = bakery.particleTexture

  override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
    val s = state?.wrapIfNeeded() ?: return emptyList()
    val key = bakery.createKey(s, side)
    return models[key] ?: bakery.bakeQuads(side, s).also { models += key to it }
  }

  override fun getItemCameraTransforms(): ItemCameraTransforms = blockItemCameraTransforms

  override fun isBuiltInRenderer(): Boolean = false

  override fun isAmbientOcclusion(): Boolean = true

  override fun isGui3d(): Boolean = false

  override fun getOverrides(): ItemOverrideList {
    return object : ItemOverrideList(emptyList()) {
      override fun handleItemState(originalModel: IBakedModel?, stack: ItemStack, world: World?, entity: EntityLivingBase?) =
        itemModel.apply { this.stack = stack }
    }
  }

  private val itemModel = object : IBakedModel {
    var stack: ItemStack? = null

    override fun getParticleTexture(): TextureAtlasSprite = bakery.particleTexture

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
      val stack = this.stack ?: return emptyList()
      val key = bakery.createKey(stack, side)
      return itemModels[key] ?: bakery.bakeItemQuads(side, stack).also { itemModels += key to it }
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms = blockItemCameraTransforms

    override fun isBuiltInRenderer(): Boolean = false

    override fun isAmbientOcclusion(): Boolean = true

    override fun isGui3d(): Boolean = true

    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
  }

  companion object {
    private var models: Map<String, List<BakedQuad>> = emptyMap()
    private var itemModels: Map<String, List<BakedQuad>> = emptyMap()

    fun clearCache() {
      models = emptyMap()
      itemModels = emptyMap()
    }

    private val blockItemCameraTransforms by lazy {
      Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(Blocks.STONE.defaultState).itemCameraTransforms
    }
  }
}