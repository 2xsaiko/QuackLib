package therealfarfetchd.quacklib.client.api.qbr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import therealfarfetchd.quacklib.common.api.qblock.QBlock

abstract class FastQBSR<in T:QBlock>:QBlockSpecialRenderer<T>() {
  override fun render(block: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
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

    renderFast(block, x, y, z, partialTicks, destroyStage, alpha, buffer)
    buffer.setTranslation(0.0, 0.0, 0.0)

    tessellator.draw()

    RenderHelper.enableStandardItemLighting()
  }

  abstract override fun renderFast(block: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float, buffer: BufferBuilder)
}