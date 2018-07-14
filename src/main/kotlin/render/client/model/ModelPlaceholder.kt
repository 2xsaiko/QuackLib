package therealfarfetchd.quacklib.render.client.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.invoke
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.render.client.model.objects.SimpleTexturedBox
import java.util.function.Function

private val Placeholder = ResourceLocation(ModID, "pablo")
private val Textures = setOf(Placeholder)

class ModelPlaceholderBlock(val rl: ResourceLocation, val block: BlockType) : IModel {

  val bb: AxisAlignedBB = block.create().getCollisionBoundingBox() ?: MCBlockType.FULL_BLOCK_AABB

  override fun getTextures(): Collection<ResourceLocation> = Textures

  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    val tex = bakedTextureGetter(Placeholder)
    return BakedModelBuilder(state, format) {
      particleTexture = tex
      transformation = BakedModelBuilder.defaultBlock
      addQuads(SimpleTexturedBox(Vec3(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat()), Vec3(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat()), tex))
    }
  }

}

class ModelPlaceholderItem(val rl: ResourceLocation, val item: ItemType) : IModel {

  override fun getTextures(): Collection<ResourceLocation> = Textures

  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    val tex = bakedTextureGetter(Placeholder)
    return BakedModelBuilder(state, format) {
      particleTexture = tex
      transformation = BakedModelBuilder.defaultItem
      addQuads(SimpleTexturedBox(Vec3(0f, 0f, 7.5f / 16f), Vec3(1f, 1f, 8.5f / 16f), tex))
    }
  }

}