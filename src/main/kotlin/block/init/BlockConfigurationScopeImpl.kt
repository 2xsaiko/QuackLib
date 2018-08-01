package therealfarfetchd.quacklib.block.init

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponentMultipart
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.block.init.BlockLinkScope
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.render.model.DataSource
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.render.model.accepts
import therealfarfetchd.quacklib.core.init.InitializationContextImpl
import therealfarfetchd.quacklib.core.init.ValidationContextImpl
import kotlin.reflect.jvm.jvmName

class BlockConfigurationScopeImpl(modid: String, override val name: String, val init: InitializationContextImpl) : BlockConfigurationScope {

  override val rl: ResourceLocation = ResourceLocation(modid, name)

  override var material: Material = Material.ROCK
  override var soundType: SoundType = SoundType.STONE
  override var hardness: Float? = 1.0f
  override var needsTool: Boolean = false
  override var validTools: Set<Tool> = emptySet()
  override var item: ItemType? = null

  override val components = mutableListOf<BlockComponent>()
  override val renderers = mutableListOf<Model>()

  override var isMultipart: Boolean = false
    private set

  @Suppress("UNCHECKED_CAST")
  override fun <T : BlockComponent> apply(component: T): T {
    components += component
    component.onApplied(this)
    if (component is BlockComponentMultipart) isMultipart = true
    return component
  }

  override fun link(op: BlockLinkScope.() -> Unit) {
    BlockLinkScopeImpl(rl).also(op)
  }

  override fun <T : Model> apply(renderer: T): T {
    renderers += renderer
    return renderer
  }

  fun validate(): Boolean {
    val vc = ValidationContextImpl("Block $name")

    if (validTools.size > 1) vc.warn("More than 1 harvest tool is currently not supported correctly.")
    hardness?.also { if (it < 0) vc.error("Hardness value is out of bounds! Must be in range [0,∞)") }

    components.forEach {
      vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
      it.validate(this, vc)
    }

    renderers
      .filter { !it.accepts<DataSource.Block>() }
      .forEach {
        vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
        vc.error("Renderer doesn't support block rendering!")
        renderers -= it
      }

    // TODO: renderer validation?
    // renderers.forEach {
    //   vc.additionalInfo = it::class.simpleName ?: it::class.qualifiedName ?: it::class.jvmName
    //   it.validate(this, vc)
    // }

    vc.additionalInfo = null

    vc.printMessages()
    return vc.isValid()
  }

}