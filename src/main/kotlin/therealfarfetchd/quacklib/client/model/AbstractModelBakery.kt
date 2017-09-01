package therealfarfetchd.quacklib.client.model

import net.minecraft.block.properties.IProperty
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState

abstract class AbstractModelBakery {

  open val ignoredProperties: Set<IProperty<*>> = emptySet()

  abstract val particleTexture: TextureAtlasSprite

  abstract fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState): List<BakedQuad>

  abstract fun bakeItemQuads(face: EnumFacing?, stack: ItemStack): List<BakedQuad>

}