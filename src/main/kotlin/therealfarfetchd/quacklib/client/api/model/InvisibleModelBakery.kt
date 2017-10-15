package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

object InvisibleModelBakery : AbstractModelBakery() {
  override val particleTexture: TextureAtlasSprite
    get() = Minecraft.getMinecraft().textureMapBlocks.missingSprite

  val emptyList = emptyList<BakedQuad>()
  const val key = "iv"

  override fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad> = emptyList
  override fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad> = emptyList
  override fun createKey(state: IExtendedBlockState, face: EnumFacing?): String = key
  override fun createKey(stack: ItemStack, face: EnumFacing?): String = key
}