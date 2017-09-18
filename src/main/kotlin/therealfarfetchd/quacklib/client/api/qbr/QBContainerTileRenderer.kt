package therealfarfetchd.quacklib.client.api.qbr

import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import therealfarfetchd.quacklib.common.api.qblock.QBContainerTile
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import kotlin.reflect.KClass

object QBContainerTileRenderer : TileEntitySpecialRenderer<QBContainerTile>() {
  override fun render(te: QBContainerTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, alpha: Float) {
    renderers[te.qb::class]?.render(te.qb, x, y, z, partialTicks, destroyStage, alpha)
  }

  override fun renderTileEntityFast(te: QBContainerTile, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, partial: Float, buffer: BufferBuilder) {
    renderers[te.qb::class]?.renderFast(te.qb, x, y, z, partialTicks, destroyStage, partial, buffer)
  }

  internal fun getRendererDispatcher() = rendererDispatcher

  internal var renderers: Map<KClass<out QBlock>, QBlockSpecialRenderer<QBlock>> = emptyMap()
}

fun <T : QBlock> KClass<T>.bindSpecialRenderer(renderer: QBlockSpecialRenderer<T>) {
  if (this in QBContainerTileRenderer.renderers) error("There's already a registered QBSR for $this!")
  @Suppress("UNCHECKED_CAST")
  QBContainerTileRenderer.renderers += this to renderer as QBlockSpecialRenderer<QBlock>
  renderer.rendererDispatcher = QBContainerTileRenderer.getRendererDispatcher()
}