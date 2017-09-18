package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.util.Random

/**
 * Created by marco on 08.07.17.
 */

fun Item.makeStack(count: Int = 1, meta: Int = 0) = ItemStack(this, count, meta)

fun Block.makeStack(count: Int = 1, meta: Int = 0) = this.getItemDropped(defaultState, Random, 0).makeStack(count, meta)

fun ItemStack.spawnAt(world: World, x: Double, y: Double, z: Double) {
  val item = EntityItem(world, x, y, z, this)
  item.setDefaultPickupDelay()
  world.spawnEntity(item)
}

fun ItemStack.spawnAt(world: World, x: Double, y: Double, z: Double, velX: Double, velY: Double, velZ: Double) {
  val item = EntityItem(world, x, y, z, this)
  item.setDefaultPickupDelay()
  item.motionX = velX
  item.motionY = velY
  item.motionZ = velZ
  world.spawnEntity(item)
}

fun ItemStack.spawnAt(world: World, pos: BlockPos) {
  spawnAt(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5)
}

fun ItemStack.spawnAt(world: World, pos: BlockPos, velX: Double, velY: Double, velZ: Double) {
  spawnAt(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, velX, velY, velZ)
}

fun ItemStack.spawnAt(world: World, e: Entity) {
  spawnAt(world, e.posX, e.posY, e.posZ)
}

fun ItemStack.spawnAt(world: World, e: Entity, velX: Double, velY: Double, velZ: Double) {
  spawnAt(world, e.posX, e.posY, e.posZ, velX, velY, velZ)
}