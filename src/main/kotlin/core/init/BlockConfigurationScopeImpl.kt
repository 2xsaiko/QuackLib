package therealfarfetchd.quacklib.core.init

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.block.render.BlockRenderer
import therealfarfetchd.quacklib.api.item.Tool
import kotlin.reflect.jvm.jvmName

class BlockConfigurationScopeImpl(modid: String, override val name: String) : BlockConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var material: Material = Material.ROCK
  override var hardness: Float? = 1.0f
  override var needsTool: Boolean = false
  override var validTools: Set<Tool> = emptySet()

  override var components: List<BlockComponent> = emptyList()

  override fun apply(component: BlockComponent) {
    components += component
    component.onApplied(this)
  }

  override fun apply(renderer: BlockRenderer) {}

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Block $name")

    if (validTools.size > 1) vc.warn("More than 1 harvest tool is currently not supported correctly.")

    components.forEach {
      vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
      it.validate(this, vc)
    }
    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}