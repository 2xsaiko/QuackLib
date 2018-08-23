package therealfarfetchd.quacklib.api.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponent
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.objects.ComponentHost
import therealfarfetchd.quacklib.api.objects.Instance
import therealfarfetchd.quacklib.api.objects.Instantiable
import therealfarfetchd.quacklib.api.objects.Registered
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.render.model.Model
import therealfarfetchd.quacklib.api.tools.PositionGrid

typealias MCBlockType = net.minecraft.block.Block
typealias MCBlock = net.minecraft.block.state.IBlockState
typealias MCBlockTile = net.minecraft.tileentity.TileEntity

interface BlockType : Instantiable, Registered, ComponentHost<BlockComponent> {

  fun create(): Block

  val behavior: BlockBehavior

  val material: Material

  val hardness: Float?

  val soundType: SoundType

  val needsTool: Boolean

  val validTools: Set<Tool>

  val model: Model

  fun Unsafe.toMCBlockType(): MCBlockType

  companion object {
    val Empty: BlockType = airBlockType
  }

}

enum class BlockRender {
  Opaque,
  Translucent,
}

enum class BlockGeometry {
  FullBlock,
  NonFull,
}

interface Block : Instance<BlockType>, BehaviorDelegate {

  val world: World

  val worldMutable: WorldMutable?

  val pos: PositionGrid

  @Deprecated("Internal usage", ReplaceWith("this"), DeprecationLevel.ERROR)
  override val block: Block
    get() = this

  fun copy(): Block

  fun Unsafe.toMCBlock(): MCBlock

  fun Unsafe.getMCTile(): MCBlockTile?

  fun Unsafe.useRef(world: World, pos: PositionGrid, asMutable: Boolean)

  fun Unsafe.useData(block: MCBlock, tile: MCBlockTile?)

}

private val airBlockType by lazy { block(ResourceLocation("minecraft", "air")) }

fun BlockType?.orEmpty(): BlockType = this ?: airBlockType

fun Block?.orEmpty(): Block = this ?: airBlockType.create()

interface UnsafeExtBlock : Unsafe {

  fun BlockType.toMCBlockType() =
    self.toMCBlockType()

  fun Block.toMCBlock() =
    self.toMCBlock()

  fun Block.getMCTile() =
    self.getMCTile()

  fun Block.useRef(world: World, pos: PositionGrid, asMutable: Boolean = world is WorldMutable) =
    self.useRef(world, pos, asMutable)

  fun Block.useData(block: MCBlock, tile: MCBlockTile?) =
    self.useData(block, tile)

}