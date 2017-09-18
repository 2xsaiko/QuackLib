package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

abstract class AbstractModelBakery {
  abstract val particleTexture: TextureAtlasSprite

  abstract fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad>

  abstract fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad>

  open fun createKey(state: IExtendedBlockState, face: EnumFacing?): String = "$face@${state.clean}@"

  open fun createKey(stack: ItemStack, face: EnumFacing?): String = "$face@${stack.item.registryName}:${stack.metadata}@"
}