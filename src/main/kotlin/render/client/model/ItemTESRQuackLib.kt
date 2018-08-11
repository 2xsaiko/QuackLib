package therealfarfetchd.quacklib.render.client.model

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.EntityRenderer
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.pipeline.LightUtil
import therealfarfetchd.quacklib.api.objects.item.toItem
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.DynDataSource
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.render.bake
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl

private val textureGetter = { location: ResourceLocation -> AtlasTextureImpl(Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite(location.toString())) }

object ItemTESRQuackLib : TileEntityItemStackRenderer() {

  val itemColors = Minecraft.getMinecraft().itemColors

  override fun renderByItem(stack: ItemStack, partialTicks: Float) {
    val tessellator = Tessellator.getInstance()
    val renderer = tessellator.buffer
    renderer.begin(7, DefaultVertexFormats.ITEM)

    val model = Minecraft.getMinecraft().renderItem.itemModelMesher.getItemModel(stack)
    val quads = model.overrides.handleItemState(model, stack, null, null)
    renderModel(renderer, quads, stack)

    val item = stack.toItem()
    val type = item.type
    val source = DataSource.Item(type, ItemRenderStateImpl(type, stack))
    val dynsource = DynDataSource.Item(item, partialTicks)
    val dm = type.model.getDynamicRender(source, dynsource, textureGetter)

    renderQuads(renderer, dm.map { it.bake(DefaultVertexFormats.ITEM) }, -1, stack)

    tessellator.draw()
  }

  private fun renderModel(renderer: BufferBuilder, model: IBakedModel, stack: ItemStack) {
    for (enumfacing in EnumFacing.values()) {
      this.renderQuads(renderer, model.getQuads(null, enumfacing, 0L), -1, stack)
    }

    this.renderQuads(renderer, model.getQuads(null, null, 0L), -1, stack)
  }

  private fun renderQuads(renderer: BufferBuilder, quads: List<BakedQuad>, color: Int, stack: ItemStack) {
    val flag = color == -1 && !stack.isEmpty
    for (bakedquad in quads) {
      var k = color

      if (flag && bakedquad.hasTintIndex()) {
        k = this.itemColors.colorMultiplier(stack, bakedquad.tintIndex)

        if (EntityRenderer.anaglyphEnable) {
          k = TextureUtil.anaglyphColor(k)
        }

        k = k or -16777216
      }

      LightUtil.renderQuadColor(renderer, bakedquad, k)
    }
  }

}