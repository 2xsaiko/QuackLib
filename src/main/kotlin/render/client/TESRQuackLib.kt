package therealfarfetchd.quacklib.render.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.render.Quad
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.DynDataSource
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.block.render.BlockRenderStateImpl
import therealfarfetchd.quacklib.objects.block.toBlock
import therealfarfetchd.quacklib.render.client.model.BakedModelBuilder
import therealfarfetchd.quacklib.render.texture.AtlasTextureImpl

object TESRQuackLib : TileEntitySpecialRenderer<TileQuackLib>() {

  override fun render(te: TileQuackLib, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
    val block = te.toBlock()
    val model = block.type.model

    if (model.needsDynamicRender())
      renderFastBridge(te, x, y, z, partialTicks, destroyStage, alpha)

    if (model.needsGlRender()) {
      GlStateManager.pushMatrix()
      GlStateManager.disableBlend()
      GlStateManager.enableCull()
      GlStateManager.translate(x, y, z)

      // FIXME: better way to do this?
      val state = te.blockType.getExtendedState(te.world.getBlockState(te.pos).getActualState(te.world, te.pos), te.world, te.pos)

      val source = DataSource.Block(block.type, BlockRenderStateImpl(block.type, state))
      val dynsource = DynDataSource.Block(block, partialTicks)

      model.renderGl(source, dynsource, texGetter)
      GlStateManager.popMatrix()
    }
  }

  fun renderFastBridge(te: TileQuackLib, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
    val tessellator = Tessellator.getInstance()
    val buffer = tessellator.buffer
    this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
    RenderHelper.disableStandardItemLighting()
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    GlStateManager.enableBlend()
    GlStateManager.disableCull()

    if (Minecraft.isAmbientOcclusionEnabled()) {
      GlStateManager.shadeModel(GL11.GL_SMOOTH)
    } else {
      GlStateManager.shadeModel(GL11.GL_FLAT)
    }

    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK)

    renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partialTicks, buffer)
    buffer.setTranslation(0.0, 0.0, 0.0)

    tessellator.draw()

    RenderHelper.enableStandardItemLighting()
  }

  override fun renderTileEntityFast(te: TileQuackLib, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, partial: Float, buffer: BufferBuilder) {
    super.renderTileEntityFast(te, x, y, z, partialTicks, destroyStage, partial, buffer)
    val block = te.toBlock()
    // FIXME: better way to do this?
    val state = te.blockType.getExtendedState(te.world.getBlockState(te.pos).getActualState(te.world, te.pos), te.world, te.pos)
    val model = block.type.model

    val source = DataSource.Block(block.type, BlockRenderStateImpl(block.type, state))
    val dynsource = DynDataSource.Block(block, partialTicks)

    val quads = try {
      model.getDynamicRender(source, dynsource, texGetter)
        .map { it.translate(Vec3(x.toFloat(), y.toFloat(), z.toFloat()) - block.pos) }
    } catch (e: Exception) {
      emptyList<Quad>()
    }

    if (quads.isNotEmpty()) {
      val model = BakedModelBuilder(DefaultVertexFormats.BLOCK) {
        particleTexture = (quads.first().texture as AtlasTextureImpl).tas
        addQuads(quads)
      }
      Minecraft.getMinecraft().blockRendererDispatcher.blockModelRenderer.renderModelFlat(world, model, state, te.pos, buffer, false, 0L)
    }
  }

}