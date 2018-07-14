package therealfarfetchd.quacklib.api.objects.world

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.api.core.Unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

typealias MCWorld = net.minecraft.world.IBlockAccess
typealias MCWorldMutable = net.minecraft.world.World

interface World {

  fun getBlock(at: PositionGrid): Block?

  fun Unsafe.toMCWorld(): MCWorld

}

interface WorldMutable : World {

  val isServer: Boolean

  val isClient: Boolean

  fun setBlock(at: PositionGrid, block: Block?): Boolean

  fun redraw(from: PositionGrid, to: PositionGrid = from)

  fun syncClient(at: PositionGrid, redraw: Boolean = false)

  fun breakBlock(at: PositionGrid, drop: Boolean = true, fx: Boolean = false): Boolean

  fun canPlaceBlockAt(block: Block, at: PositionGrid, hitSide: Facing, placer: Entity?, checkEntityCollision: Boolean = true): Boolean

  fun checkForCollision(aabb: AxisAlignedBB, except: Entity? = null): Boolean

  fun playSound(player: EntityPlayer, pos: PositionGrid, sound: SoundEvent, category: SoundCategory, volume: Float, pitch: Float)

  override fun Unsafe.toMCWorld(): MCWorldMutable

}

interface UnsafeExtWorld : Unsafe {

  fun World.toMCWorld() = self.toMCWorld()

  fun WorldMutable.toMCWorld() = self.toMCWorld()

}