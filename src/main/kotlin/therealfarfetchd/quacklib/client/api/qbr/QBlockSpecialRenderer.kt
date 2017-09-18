package therealfarfetchd.quacklib.client.api.qbr

import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.qblock.QBlock

abstract class QBlockSpecialRenderer<in T : QBlock> {
  lateinit var rendererDispatcher: TileEntityRendererDispatcher

  abstract fun render(block: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float)

  open fun renderFast(block: T, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float, buffer: BufferBuilder) {}

  protected fun bindTexture(location: ResourceLocation) {
    rendererDispatcher.renderEngine?.bindTexture(location)
  }

  /**
   * Sets whether to use the light map when rendering. Disabling this allows rendering ignoring lighting, which can be
   * useful for floating text, e.g.
   */
  protected fun setLightmapDisabled(disabled: Boolean) {
    GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)

    if (disabled) {
      GlStateManager.disableTexture2D()
    } else {
      GlStateManager.enableTexture2D()
    }

    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)
  }
}