package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.ForgeRegistries
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockBehavior
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import kotlin.reflect.KClass

class BlockTypeImpl(val conf: BlockConfiguration) : BlockType {

  lateinit var block: MCBlockType

  override val components: List<BlockComponent> = conf.components

  val componentPresence: MutableMap<KClass<out BlockComponent>, Boolean> = mutableMapOf()

  override val behavior = StandardBlockBehavior(this)

  override val material: Material = conf.material

  override val needsTool: Boolean = conf.needsTool

  override val validTools: Set<Tool> = conf.validTools

  override val hardness: Float? = conf.hardness

  override val soundType: SoundType = conf.soundType

  override fun create(): Block = BlockImpl(this)

  override fun hasComponent(type: KClass<out BlockComponent>): Boolean =
    componentPresence.computeIfAbsent(type) { super.hasComponent(type) }

  override fun Unsafe.toMCBlockType(): MCBlockType = block

  override val registryName: ResourceLocation
    get() = conf.rl

  companion object {

    val map = mutableMapOf<ResourceLocation, BlockType>()

    fun getBlock(mc: MCBlockType): BlockType {
      if (mc is BlockQuackLib) return mc.type

      val rl = mc.registryName!!

      return map.getOrPut(rl) { BlockTypeImpl.Vanilla(mc) }
    }

    fun getBlock(rl: ResourceLocation): BlockType? {
      map[rl]?.also { return it }

      val block = with(ForgeRegistries.BLOCKS) { getValue(rl).takeIf { containsKey(rl) } }
                  ?: return null

      val bt = (block as? BlockQuackLib)?.type ?: BlockTypeImpl.Vanilla(block)
      map[rl] = bt
      return bt
    }

    fun addBlock(type: BlockTypeImpl) {
      map[type.registryName] = type
    }

    fun associateBlock(type: BlockTypeImpl, mc: MCBlockType) {
      type.block = mc
    }

  }

  @Suppress("DEPRECATION")
  class Vanilla(val block: MCBlockType) : BlockType {

    val dstate = block.defaultState

    override fun create(): Block = BlockImpl(this)

    override val material: Material = dstate.material

    override val validTools: Set<Tool> = block.getHarvestTool(dstate)?.let { setOf(Tool(it, block.getHarvestLevel(dstate))) }.orEmpty()

    override val needsTool: Boolean = validTools.isNotEmpty()

    override val hardness: Float? = block.blockHardness

    override val soundType: SoundType = block.soundType

    override val behavior: BlockBehavior = VanillaBlockBehavior(block)

    override fun Unsafe.toMCBlockType(): MCBlockType = block

    override val registryName: ResourceLocation
      get() = block.registryName!!

    override val components: List<BlockComponent>
      get() = emptyList()

  }

}