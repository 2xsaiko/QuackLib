package therealfarfetchd.quacklib.render.client.model

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.IModel
import net.minecraftforge.common.model.IModelState
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.extensions.invoke
import therealfarfetchd.quacklib.core.ModID
import therealfarfetchd.quacklib.render.client.model.objects.SimpleTexturedBox
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl
import java.util.function.Function

object ModelError : IModel {

  val ErrorTex = ResourceLocation(ModID, "error")

  private val Textures = setOf(ErrorTex)

  override fun getTextures() = Textures

  override fun bake(state: IModelState, format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    return bake(format, bakedTextureGetter)
  }

  fun bake(format: VertexFormat, bakedTextureGetter: Function<ResourceLocation, TextureAtlasSprite>): IBakedModel {
    val tex = bakedTextureGetter(ErrorTex)
    return BakedModelBuilder(format) {
      particleTexture = tex
      transformation = BakedModelBuilder.defaultBlock
      addQuads(SimpleTexturedBox(Vec3(0, 0, 0), Vec3(1, 1, 1), AtlasTextureImpl(tex), true))
    }
  }

}