package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.state.BlockFaceShape
import net.minecraft.tileentity.TileEntity
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlock
import therealfarfetchd.quacklib.api.objects.block.MCBlockTile
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.objects.world.BlockAccessVoid
import therealfarfetchd.quacklib.objects.world.toWorld
import therealfarfetchd.quacklib.tools.copy

class BlockImpl(
  override val type: BlockType,
  override var world: World,
  override var pos: PositionGrid,
  var state: MCBlock,
  var tile: MCBlockTile?
) : Block {

  constructor(block: BlockType) : this(
    block,
    BlockAccessVoid.toWorld(),
    PositionGrid(0, 64, 0)
  )

  @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  constructor(block: BlockType, world: World, pos: PositionGrid) : this(
    block, world, pos,
    unsafe { block.toMCBlockType().defaultState },
    unsafe { block.toMCBlockType().let { it.createTileEntity((world as? WorldMutable)?.toMCWorld(), it.defaultState) } }
  )

  override val behavior = type.behavior

  override var worldMutable: WorldMutable? = world as? WorldMutable

  override fun getFaceShape(side: Facing): BlockFaceShape {
    return behavior.getFaceShape(this, side)
  }

  override fun copy(): Block {
    return BlockImpl(type, world, pos, state, tile?.copy())
  }

  override fun Unsafe.toMCBlock(): MCBlock = state

  override fun Unsafe.getMCTile(): MCBlockTile? = tile

  override fun Unsafe.useRef(world: World, pos: PositionGrid, asMutable: Boolean) {
    this@BlockImpl.world = world
    this@BlockImpl.pos = pos
    if (world is WorldMutable && asMutable)
      this@BlockImpl.worldMutable = world
  }

  override fun Unsafe.useData(block: MCBlock, tile: MCBlockTile?) {
    this@BlockImpl.state = block
    this@BlockImpl.tile = tile
  }

  override fun toString(): String {
    return "$state;$tile ($type)"
  }

  companion object {

    fun createExistingFromWorld(world: World, pos: PositionGrid): BlockImpl {
      return unsafe {
        val pos1 = pos.toMCVec3i()
        val state = world.toMCWorld().getBlockState(pos1)
        val tile = world.toMCWorld().getTileEntity(pos1)
        val block = BlockTypeImpl.getBlock(state.block)

        BlockImpl(block, world, pos, state, tile)
      }
    }

    fun createExistingFromWorld(world: World, pos: PositionGrid, state: MCBlock): BlockImpl {
      return unsafe {
        val pos1 = pos.toMCVec3i()
        val tile = world.toMCWorld().getTileEntity(pos1)
        val block = BlockTypeImpl.getBlock(state.block)

        BlockImpl(block, world, pos, state, tile)
      }
    }

    fun createExistingFromTile(te: TileEntity): BlockImpl {
      return unsafe {
        val pos = te.pos
        val world = te.world
        val state = world.getBlockState(pos)
        val block = BlockTypeImpl.getBlock(state.block)

        BlockImpl(block, world.toWorld(), pos.toVec3i(), state, te)
      }
    }

  }

}

fun TileEntity.toBlock(): Block =
  BlockImpl.createExistingFromTile(this)