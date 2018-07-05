package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockBehavior
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType

class DeferredBlockTypeImpl(override val registryName: ResourceLocation) : BlockType {

  var realInstance: BlockType? = null

  override fun create(): Block =
    realInstance?.create()
    ?: crash()

  override val components: List<BlockComponent>
    get() = realInstance?.components
            ?: crash()

  override val behavior: BlockBehavior
    get() = realInstance?.behavior
            ?: crash()

  override val material: Material
    get() = realInstance?.material
            ?: crash()

  override val hardness: Float?
    get() = realInstance?.hardness
            ?: crash()

  override val soundType: SoundType
    get() = realInstance?.soundType
            ?: crash()

  override val needsTool: Boolean
    get() = realInstance?.needsTool
            ?: crash()

  override val validTools: Set<Tool>
    get() = realInstance?.validTools
            ?: crash()

  override val Unsafe.mc: MCBlockType
    get() = unsafe { realInstance?.mc }
            ?: crash()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun crash(): Nothing = error("Block not resolved yet! Come back after init is done")

  companion object {
    var instances: Set<DeferredBlockTypeImpl> = emptySet()
    var isInit = true
  }

}