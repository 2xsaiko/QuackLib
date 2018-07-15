package therealfarfetchd.quacklib.render.texture

import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec2
import therealfarfetchd.math.Vec2i
import therealfarfetchd.quacklib.api.render.texture.AtlasTexture

class AtlasTextureImpl(val tas: TextureAtlasSprite) : AtlasTexture {

  override val sourceTexture: ResourceLocation = ResourceLocation(tas.iconName)

  override val min: Vec2 = Vec2(tas.minU, tas.minV)

  override val max: Vec2 = Vec2(tas.maxU, tas.maxV)

  override val size: Vec2i = Vec2i(tas.iconWidth, tas.iconHeight)

  override val texture: ResourceLocation = TextureMap.LOCATION_BLOCKS_TEXTURE

}