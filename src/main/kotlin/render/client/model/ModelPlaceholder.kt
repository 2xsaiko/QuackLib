package therealfarfetchd.quacklib.render.client.model

import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.render.model.SimpleModel
import therealfarfetchd.quacklib.core.ModID

private val Placeholder = ResourceLocation(ModID, "pablo")

class ModelPlaceholderBlock(val bb: AxisAlignedBB) : SimpleModel() {

  val tex = useTexture(Placeholder)

  override fun getParticleTexture(): PreparedTexture = tex

  override fun ModelContext.addObjects() {
    add(Box) {
      from(bb.minX.toFloat(), bb.minY.toFloat(), bb.minZ.toFloat())
      to(bb.maxX.toFloat(), bb.maxY.toFloat(), bb.maxZ.toFloat())

      textureAll(tex) {
        uv(0f, 0f, 1f, 1f)
      }
    }
  }

}

class ModelPlaceholderItem : SimpleModel() {

  val tex = useTexture(Placeholder)

  override fun getParticleTexture(): PreparedTexture = tex

  override fun isItemTransformation(): Boolean = true

  override fun ModelContext.addObjects() {
    add(InflatedTexture) {
      texture(tex)
    }
  }

}