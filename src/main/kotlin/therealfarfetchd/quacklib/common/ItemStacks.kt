package therealfarfetchd.quacklib.common

import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by marco on 08.07.17.
 */

fun Item.makeStack(count: Int = 1, meta: Int = 0) = ItemStack(this, count, meta)

fun ItemStack.spawnAt(world: World, x: Double, y: Double, z: Double) {
  val item = EntityItem(world, x, y, z)
  world.spawnEntity(item)
}

fun ItemStack.spawnAt(world: World, x: Double, y: Double, z: Double, velX: Double, velY: Double, velZ: Double) {
  val item = EntityItem(world, x, y, z)
  item.setVelocity(velX, velY, velZ)
  world.spawnEntity(item)
}

fun ItemStack.spawnAt(world: World, pos: BlockPos) {
  spawnAt(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
}

fun ItemStack.spawnAt(world: World, pos: BlockPos, velX: Double, velY: Double, velZ: Double) {
  spawnAt(world, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), velX, velY, velZ)
}

fun ItemStack.spawnAt(world: World, e: Entity) {
  spawnAt(world, e.posX, e.posY, e.posZ)
}

fun ItemStack.spawnAt(world: World, e: Entity, velX: Double, velY: Double, velZ: Double) {
  spawnAt(world, e.posX, e.posY, e.posZ, velX, velY, velZ)
}