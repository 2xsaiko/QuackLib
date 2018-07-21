package therealfarfetchd.quacklib.client.api.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.property.IExtendedBlockState
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.math.Vec3

sealed class StandardComposedModel(val components: List<IModel>) : IModel {
  override val particleTexture: TextureAtlasSprite
    get() = components.firstOrNull()?.particleTexture ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite

  override fun bakeQuads(face: EnumFacing?, state: IExtendedBlockState, vf: VertexFormat) =
    components.flatMap { it.bakeQuads(face, state, vf) }

  override fun bakeItemQuads(face: EnumFacing?, stack: ItemStack, vf: VertexFormat) =
    components.flatMap { it.bakeItemQuads(face, stack, vf) }

  override fun createKey(stack: ItemStack, face: EnumFacing?) =
    components.joinToString(separator = ";", prefix = "COMPOSED[", postfix = "]") { it.createKey(stack, face) }

  override fun createKey(state: IExtendedBlockState, face: EnumFacing?) =
    components.joinToString(separator = ";", prefix = "COMPOSED[", postfix = "]") { it.createKey(state, face) }
}

class DynamicComposedModel<in T : QBlock>(components: List<IModel>, val dynComponents: List<IDynamicModel<T>>
) : StandardComposedModel(components), IDynamicModel<T> {
  override fun bakeDynamicQuads(block: T, playerPos: Vec3) = dynComponents.flatMap { it.bakeDynamicQuads(block, playerPos) }
}