package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockBehavior
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType

class CreatedBlockTypeImpl(override val registryName: ResourceLocation, val def: BlockConfiguration) : BlockType {

  init {
    instances += this
  }

  var realInstance: BlockType? = null

  override fun create(): Block {
    return realInstance?.create()
           ?: crash()
  }

  override val components: List<BlockComponent>
    get() = realInstance?.components
            ?: def.components

  override val behavior: BlockBehavior
    get() = realInstance?.behavior
            ?: crash()

  override val material: Material
    get() = realInstance?.material
            ?: def.material

  override val hardness: Float?
    get() = realInstance?.hardness
            ?: def.hardness

  override val soundType: SoundType
    get() = realInstance?.soundType
            ?: def.soundType

  override val needsTool: Boolean
    get() = realInstance?.needsTool
            ?: def.needsTool

  override val validTools: Set<Tool>
    get() = realInstance?.validTools
            ?: def.validTools

  override val Unsafe.mc: MCBlockType
    get() = unsafe { realInstance?.mc }
            ?: crash()

  @Suppress("NOTHING_TO_INLINE")
  private inline fun crash(): Nothing = error("Block not resolved yet! Come back after init is done")


  companion object {
    var instances: Set<CreatedBlockTypeImpl> = emptySet()
  }

}