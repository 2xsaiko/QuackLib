package therealfarfetchd.quacklib.api.render.texture

import net.minecraft.util.ResourceLocation

interface AtlasTexture : Texture {

  /**
   * The actual texture that got stitched into the atlas.
   */
  val sourceTexture: ResourceLocation

}