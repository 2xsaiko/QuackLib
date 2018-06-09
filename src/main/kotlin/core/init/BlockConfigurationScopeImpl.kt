package therealfarfetchd.quacklib.core.init

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.Tool
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.render.BlockRenderer
import therealfarfetchd.quacklib.api.core.init.block.BlockConfigurationScope

class BlockConfigurationScopeImpl(modid: String, override val name: String) : BlockConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var material: Material = Material.ROCK
  override var hardness: Float? = 1.0f
  override var needsTool: Boolean = false
  override var validTools: Set<Tool> = emptySet()

  var components: List<BlockComponent> = emptyList()

  override fun apply(component: BlockComponent) {
    components += component
    component.onApplied(this)
  }

  override fun apply(renderer: BlockRenderer) {
  }

}