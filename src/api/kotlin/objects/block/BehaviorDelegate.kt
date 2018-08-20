package therealfarfetchd.quacklib.api.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.ImportedValue
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

interface BehaviorDelegate {

  val block: Block

  val behavior: BlockBehavior

  operator fun <T : BlockDataPart> get(token: PartAccessToken<T>): T =
    behavior.getPart(block, token)

  operator fun <T> get(value: ImportedValue<T>): T =
    behavior.getImported(block, value)

  fun onActivated(player: EntityPlayer, hand: EnumHand, facing: Facing, hitVec: Vec3) =
    behavior.onActivated(block, player, hand, facing, hitVec)

  fun onPlaced(player: EntityPlayer, item: Item) =
    behavior.onPlaced(block, player, item)

  fun onRemoved() =
    behavior.onRemoved(block)

  fun onNeighborChanged(side: EnumFacing) =
    behavior.onNeighborChanged(block, side)

  fun getFaceShape(side: Facing): BlockFaceShape =
    behavior.getFaceShape(block, side)

  fun getSoundType(entity: Entity?): SoundType =
    behavior.getSoundType(block, entity)

  fun getCollisionBoundingBox(): AxisAlignedBB? =
    behavior.getCollisionBoundingBox(block)

  fun getCollisionBoundingBoxes(): List<AxisAlignedBB> =
    behavior.getCollisionBoundingBoxes(block)

  fun getRaytraceBoundingBox(): AxisAlignedBB? =
    behavior.getRaytraceBoundingBox(block)

  fun getRaytraceBoundingBoxes(): List<AxisAlignedBB> =
    behavior.getRaytraceBoundingBoxes(block)

  fun getSelectedBoundingBox(result: RayTraceResult) =
    behavior.getSelectedBoundingBox(block, result)

  fun getStrongPower(side: Facing) =
    behavior.getStrongPower(block, side)

  fun getWeakPower(side: Facing) =
    behavior.getWeakPower(block, side)

  fun getDrops(fortune: Int = 0): Collection<Item> =
    behavior.getDrops(block, fortune)

  fun getPickBlock(target: RayTraceResult, player: EntityPlayer): Item =
    behavior.getPickBlock(block, target, player)

  fun canConnectRedstone(side: Facing) =
    behavior.canConnectRedstone(block, side)

  fun isReplacable(): Boolean =
    behavior.isReplacable(block)

  fun raytrace(from: Vec3, to: Vec3): RayTraceResult? =
    behavior.raytrace(block, from, to)

  fun canPlaceBlockAt(world: World, pos: PositionGrid, facing: Facing?): Boolean =
    behavior.canPlaceBlockAt(world, pos, facing)

}