package therealfarfetchd.quacklib.testmod

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.item.component.ItemComponentRenderProperties
import therealfarfetchd.quacklib.api.item.component.fix
import therealfarfetchd.quacklib.api.item.component.renderProperty
import kotlin.math.sin

class DummyCompItem : ItemComponentRenderProperties {

  override val rl: ResourceLocation = ResourceLocation("qltestmod:dummy")

  val rot = renderProperty<Float>("rotation") {
    output { ((System.currentTimeMillis() % (720 * 50) / 50.0f) * 0.5f) }
  } fix this

  val scale = renderProperty<Float>("scale") {
    output { (sin((System.currentTimeMillis() % (31415 * 50) / 50f) * 0.1f) + 1.75f) / 2.75f }
  } fix this

}

