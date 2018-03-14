package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.isServer
import therealfarfetchd.quacklib.common.api.extensions.minus
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.block.MultiblockExtension

interface IQBlockMultiblock {
  private val qb: QBlock
    get() = this as QBlock

  fun FillBlocksScope.fillBlocks()

  fun onRemoteBreak(offset: BlockPos, player: EntityPlayer) {
    qb.dismantle()
  }

  fun onActivatedRemote(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, from: BlockPos, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return qb.onActivated(player, hand, facing, hitX, hitY, hitZ)
  }
}

fun IQBlockMultiblock.fillBlocks(test: Boolean = false) = fillBlocks0(test, false)

internal fun IQBlockMultiblock.fillBlocks0(test: Boolean = false, prePlaceTest: Boolean = false): Boolean {
  this as QBlock // if this is not the case, you're doing something seriously wrong, bucko

  val container = if (!prePlaceTest) this.container as QBContainerTileMultiblock else null
  val fbs = FillBlocksScopeImpl(pos, world, container?.extBlocks ?: emptyList())
  try {
    fbs.fillBlocks()
    if (!test && world.isServer) {
      fbs.commitToWorld()
      container?.extBlocks = fbs.extBlocks
      if (!prePlaceTest) dataChanged()
    }
  } catch (e: ScopeControlFlow) {
    // do nothing, it was aborted
    return false
  }
  return true
}

interface FillBlocksScope {
  // All positions are relative!

  fun removeExtBlocks()

  fun placeExtension(pos: BlockPos): Boolean

  fun fill(from: BlockPos, to: BlockPos): Int

  fun cancel(): Nothing

  fun cancelIf(condition: Boolean)

  fun cancelUnless(condition: Boolean)
}

@Suppress("NOTHING_TO_INLINE")
inline fun FillBlocksScope.fillOrCancel(from: BlockPos, to: BlockPos) = cancelIf(fill(from, to) > 0)

@Suppress("NOTHING_TO_INLINE")
inline fun FillBlocksScope.placeOrCancel(pos: BlockPos) = cancelIf(!placeExtension(pos))

private class FillBlocksScopeImpl(val posOrigin: BlockPos, val world: World, val currentExtBlocks: Collection<BlockPos>) : FillBlocksScope {
  var extBlocks: Collection<BlockPos> = currentExtBlocks

  override fun removeExtBlocks() {
    extBlocks = emptyList()
  }

  override fun placeExtension(pos: BlockPos): Boolean {
    if (pos == BlockPos.ORIGIN) return true
    if (!checkState(pos)) return false
    extBlocks += pos
    return true
  }

  override fun fill(from: BlockPos, to: BlockPos): Int {
    return BlockPos.getAllInBox(from, to)
      .filter { !placeExtension(it) }
      .count()
  }

  fun checkState(_pos: BlockPos): Boolean {
    val pos = _pos + posOrigin
    if (_pos in extBlocks || _pos in currentExtBlocks) return true
    val bs = world.getBlockState(pos)
    return bs.block.isReplaceable(world, pos)
  }

  override fun cancel(): Nothing = throw ScopeControlFlow()

  override fun cancelIf(condition: Boolean) {
    if (condition) cancel()
  }

  override fun cancelUnless(condition: Boolean) {
    if (!condition) cancel()
  }

  fun commitToWorld() {
    val toRemove = currentExtBlocks - extBlocks
    val toAdd = extBlocks - currentExtBlocks

    toRemove.map { it + posOrigin }.forEach { world.setBlockToAir(it) }
    toAdd.map { it + posOrigin }.forEach {
      world.setBlockState(it, MultiblockExtension.Block.defaultState)
      (world.getTileEntity(it) as MultiblockExtension.Tile).rootOffset = posOrigin - it
    }
  }
}

private class ScopeControlFlow : Throwable()