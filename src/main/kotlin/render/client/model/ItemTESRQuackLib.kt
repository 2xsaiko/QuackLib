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
import net.minecraftforge.client.model.pipeline.LightUtil
import org.lwjgl.opengl.GL11
import therealfarfetchd.quacklib.api.objects.item.toItem
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.DynDataSource
import therealfarfetchd.quacklib.item.render.ItemRenderStateImpl
import therealfarfetchd.quacklib.render.bake
import therealfarfetchd.quacklib.render.client.texGetter

object ItemTESRQuackLib : TileEntityItemStackRenderer() {

  val itemColors = Minecraft.getMinecraft().itemColors

  override fun renderByItem(stack: ItemStack, partialTicks: Float) {
    val tessellator = Tessellator.getInstance()
    val renderer = tessellator.buffer
    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)

    val model = Minecraft.getMinecraft().renderItem.itemModelMesher.getItemModel(stack)
    val quads = model.overrides.handleItemState(model, stack, null, null)
    renderModel(renderer, quads, stack)

    val item = stack.toItem()
    val type = item.type
    val source = DataSource.Item(type, ItemRenderStateImpl(type, stack))
    val dynsource = DynDataSource.Item(item, partialTicks)
    val dm = try {
      type.model.getDynamicRender(source, dynsource, texGetter)
    } catch (e: Exception) {
      emptyList<Quad>()
    }

    renderQuads(renderer, dm.map { it.bake(DefaultVertexFormats.ITEM) }, -1, stack)

    tessellator.draw()
  }

  private fun renderModel(renderer: BufferBuilder, model: IBakedModel, stack: ItemStack) {
    for (enumfacing in EnumFacing.values()) {
      this.renderQuads(renderer, model.getQuads(null, enumfacing, 0), -1, stack)
    }

    this.renderQuads(renderer, model.getQuads(null, null, 0), -1, stack)
  }

  private fun renderQuads(renderer: BufferBuilder, quads: List<BakedQuad>, color: Int, stack: ItemStack) {
    val flag = color == -1 && !stack.isEmpty
    for (quad in quads) {
      var k = color

      if (flag && quad.hasTintIndex()) {
        k = this.itemColors.colorMultiplier(stack, quad.tintIndex)

        if (EntityRenderer.anaglyphEnable) {
          k = TextureUtil.anaglyphColor(k)
        }

        k = k or 0xFF000000u.toInt()
      }

      LightUtil.renderQuadColor(renderer, quad, k)
    }
  }

}