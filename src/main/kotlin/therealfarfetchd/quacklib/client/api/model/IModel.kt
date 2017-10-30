package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

interface IModel {
  val particleTexture: TextureAtlasSprite

  fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad>

  fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad>

  fun getParticleTexture(state: IExtendedBlockState): TextureAtlasSprite = particleTexture

  fun createKey(state: IExtendedBlockState, face: EnumFacing?): String = "$face@${state.block.registryName}@${state.clean}@"

  fun createKey(stack: ItemStack, face: EnumFacing?): String = "$face@${stack.item.registryName}:${stack.metadata}@"
}