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
    unsafe { block.mc.defaultState },
    unsafe { block.mc.createTileEntity((world as? WorldMutable)?.mc, block.mc.defaultState) }
  )

  override val behavior = type.behavior

  override var worldMutable: WorldMutable? = world as? WorldMutable

  override fun getFaceShape(side: Facing): BlockFaceShape {
    return behavior.getFaceShape(this, side)
  }

  override val Unsafe.mcBlock: MCBlock
    get() = state

  override val Unsafe.mcTile: MCBlockTile?
    get() = tile

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

  companion object {

    fun createExistingFromWorld(world: World, pos: PositionGrid): BlockImpl {
      return unsafe {
        val pos1 = pos.toMCVec3i()
        val state = world.mc.getBlockState(pos1)
        val tile = world.mc.getTileEntity(pos1)
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