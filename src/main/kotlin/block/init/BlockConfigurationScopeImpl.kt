package therealfarfetchd.quacklib.block.init

import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.AppliedComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.block.init.BlockDataLinkScope
import therealfarfetchd.quacklib.api.block.render.BlockRenderer
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.block.component.AppliedComponentImpl
import therealfarfetchd.quacklib.core.init.InitializationContextImpl
import therealfarfetchd.quacklib.core.init.ValidationContextImpl
import kotlin.reflect.jvm.jvmName

class BlockConfigurationScopeImpl(modid: String, override val name: String, val init: InitializationContextImpl) : BlockConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var material: Material = Material.ROCK
  override var hardness: Float? = 1.0f
  override var needsTool: Boolean = false
  override var validTools: Set<Tool> = emptySet()
  override var item: ItemReference? = null

  override var components: List<BlockComponent> = emptyList()

  override fun <T : BlockComponent> apply(component: T): AppliedComponent<T> {
    components += component
    component.onApplied(this)
    return AppliedComponentImpl(component)
  }

  override fun link(op: BlockDataLinkScope.() -> Unit) {
    BlockDataLinkScopeImpl().also(op)
  }

  override fun apply(renderer: BlockRenderer) {}

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Block $name")

    if (validTools.size > 1) vc.warn("More than 1 harvest tool is currently not supported correctly.")
    hardness?.also { if (it < 0) vc.error("Hardness value is out of bounds! Must be in range [0,∞)") }

    components.forEach {
      vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
      it.validate(this, vc)
    }
    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}