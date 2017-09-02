package therealfarfetchd.quacklib.client.model

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.entity.EntityLivingBase
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraftforge.common.property.IExtendedBlockState

class CachedBakedModel(private val bakery: AbstractModelBakery) : IBakedModel {
  override fun getParticleTexture(): TextureAtlasSprite = bakery.particleTexture

  override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
    if (state == null) return emptyList()
    if (state !in models) {
      models += state to bakery.bakeQuads(side, state as IExtendedBlockState)
    }
    return models[state]!!
  }

  override fun getItemCameraTransforms(): ItemCameraTransforms = blockItemCameraTransforms

  override fun isBuiltInRenderer(): Boolean = false

  override fun isAmbientOcclusion(): Boolean = true

  override fun isGui3d(): Boolean = false

  override fun getOverrides(): ItemOverrideList {
    return object : ItemOverrideList(emptyList()) {
      override fun handleItemState(originalModel: IBakedModel?, stack: ItemStack, world: World?, entity: EntityLivingBase?): IBakedModel = itemModel.apply { this.stack = stack }
    }
  }

  private val itemModel = object : IBakedModel {
    var stack: ItemStack? = null

    override fun getParticleTexture(): TextureAtlasSprite = bakery.particleTexture

    override fun getQuads(state: IBlockState?, side: EnumFacing?, rand: Long): List<BakedQuad> {
      val stack = this.stack ?: return emptyList()
      val key = stack.item to stack.metadata
      if (key !in itemModels) {
        itemModels += key to bakery.bakeItemQuads(side, stack)
      }
      return itemModels[key]!!
    }

    override fun getItemCameraTransforms(): ItemCameraTransforms = blockItemCameraTransforms

    override fun isBuiltInRenderer(): Boolean = false

    override fun isAmbientOcclusion(): Boolean = true

    override fun isGui3d(): Boolean = true

    override fun getOverrides(): ItemOverrideList = ItemOverrideList.NONE
  }

  companion object {
    private var models: Map<IBlockState, List<BakedQuad>> = emptyMap()
    private var itemModels: Map<Pair<Item, Int>, List<BakedQuad>> = emptyMap()

    fun clearCache() {
      models = emptyMap()
      itemModels = emptyMap()
    }

    private val blockItemCameraTransforms by lazy {
      Minecraft.getMinecraft().blockRendererDispatcher.blockModelShapes.getModelForState(Blocks.STONE.defaultState).itemCameraTransforms
    }
  }
}