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

  val Unsafe.mc: MCBlockType

}

interface Block : Instance<BlockType>, BehaviorDelegate {

  val world: World

  val worldMutable: WorldMutable?

  val pos: PositionGrid

  override val block: Block
    get() = this

  val Unsafe.mcBlock: MCBlock

  val Unsafe.mcTile: MCBlockTile?

  fun Unsafe.useRef(world: World, pos: PositionGrid, asMutable: Boolean)

  fun Unsafe.useData(block: MCBlock, tile: MCBlockTile?)

}

private val airBlockType by lazy { block(ResourceLocation("minecraft", "air")) }

fun BlockType?.orEmpty(): BlockType = this ?: airBlockType

fun Block?.orEmpty(): Block = this ?: airBlockType.create()

interface UnsafeExtBlock : Unsafe {

  val BlockType.mc
    get() = self.mc

  val Block.mcBlock
    get() = self.mcBlock

  val Block.mcTile
    get() = self.mcTile

  fun Block.useRef(world: World, pos: PositionGrid, asMutable: Boolean = world is WorldMutable) =
    self.useRef(world, pos, asMutable)

  fun Block.useData(block: MCBlock, tile: MCBlockTile?) =
    self.useData(block, tile)

}