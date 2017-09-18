package therealfarfetchd.quacklib.client.api.qbr

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11
import therealfarfetchd.quacklib.client.api.model.DynamicModelBakery
import therealfarfetchd.quacklib.common.api.qblock.QBlock

class DynamicModelRenderer<in T : QBlock>(val bakery: DynamicModelBakery<T>) : QBlockSpecialRenderer<T>() {
  override fun render(block: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
    pushMatrix()
    translate(x, y, z)

    bindTexture(Minecraft.getMinecraft().textureMapBlocks.glTextureId)

    blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
    enableBlend()

    if (Minecraft.isAmbientOcclusionEnabled()) {
      shadeModel(GL11.GL_SMOOTH)
    } else {
      shadeModel(GL11.GL_FLAT)
    }

    val t = Tessellator.getInstance()

    val buf = t.buffer
    buf.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM)
    bakery.bakeDynamicQuads(block).forEach {
      buf.addVertexData(it.vertexData)
    }
    t.draw()

    popMatrix()
  }
}