package therealfarfetchd.quacklib.objects.world

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.world.MCWorld
import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.objects.block.BlockImpl
import therealfarfetchd.quacklib.tools.copy

class WorldImpl(val world: MCWorld) : World {

  override fun getBlock(at: PositionGrid): Block? {
    if (world.isAirBlock(at.toMCVec3i())) return null
    return BlockImpl.createExistingFromWorld(this, at)
  }

  override fun Unsafe.toMCWorld(): MCWorld = world

}

class WorldMutableImpl(val world: MCWorldMutable) : WorldMutable {

  override val isServer: Boolean = !world.isRemote

  override val isClient: Boolean = world.isRemote

  override val worldTime: Long
    get() = world.worldTime

  override val totalTime: Long
    get() = world.totalWorldTime

  override fun getBlock(at: PositionGrid): Block? {
    if (world.isAirBlock(at.toMCVec3i())) return null
    return BlockImpl.createExistingFromWorld(this, at)
  }

  override fun setBlock(at: PositionGrid, block: Block?): Boolean {
    val mcpos = at.toMCVec3i()

    if (block == null) return world.setBlockToAir(mcpos)

    return unsafe {
      var state = block.toMCBlock()
      val tile = block.getMCTile()?.copy()
      if (!world.setBlockState(mcpos, state)) false
      else {
        state = world.getBlockState(mcpos)
        if (state.block.hasTileEntity(state)) {
          world.setTileEntity(mcpos, tile)
          block.useData(state, tile)
        }
        true
      }
    }
  }

  override fun redraw(from: PositionGrid, to: PositionGrid) {
    world.markBlockRangeForRenderUpdate(from.toMCVec3i(), to.toMCVec3i())
  }

  override fun syncClient(at: PositionGrid, redraw: Boolean) {
    val mcpos = at.toMCVec3i()
    val state = world.getBlockState(mcpos)
    world.notifyBlockUpdate(mcpos, state, state, 3)
  }

  override fun breakBlock(at: PositionGrid, drop: Boolean, fx: Boolean): Boolean {
    return world.destroyBlock(at.toMCVec3i(), drop)
  }

  override fun canPlaceBlockAt(block: Block, at: PositionGrid, hitSide: Facing, placer: Entity?, checkEntityCollision: Boolean): Boolean {
    if (checkEntityCollision) {
      val bb = block.getCollisionBoundingBox()?.offset(at.toMCVec3i())
      if (bb != null && checkForCollision(bb)) return false
    }

    return unsafe {
      world.mayPlace(block.type.toMCBlockType(), at.toMCVec3i(), true, hitSide, placer)
    }
  }

  override fun checkForCollision(aabb: AxisAlignedBB, except: Entity?): Boolean {
    return !world.checkNoEntityCollision(aabb, except)
  }

  override fun playSound(player: EntityPlayer, pos: PositionGrid, sound: SoundEvent, category: SoundCategory, volume: Float, pitch: Float) {
    world.playSound(player, pos.toMCVec3i(), sound, category, volume, pitch)
  }

  override fun Unsafe.toMCWorld(): MCWorldMutable = world

}

fun MCWorld.toWorld() =
  if (this is MCWorldMutable) WorldMutableImpl(this)
  else WorldImpl(this)

fun MCWorldMutable.toWorld() =
  WorldMutableImpl(this)