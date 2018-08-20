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

interface BlockBehavior {

  fun <T : BlockDataPart> getPart(block: Block, token: PartAccessToken<T>): T

  fun <T> getImported(block: Block, value: ImportedValue<T>): T

  fun onActivated(block: Block, player: EntityPlayer, hand: EnumHand, facing: Facing, hitVec: Vec3): Boolean

  fun onNeighborChanged(block: Block, side: EnumFacing)

  fun onPlaced(block: Block, player: EntityPlayer, item: Item)

  fun onRemoved(block: Block)

  fun getFaceShape(self: Block, side: Facing): BlockFaceShape

  fun getSoundType(block: Block, entity: Entity?): SoundType

  fun getCollisionBoundingBox(block: Block): AxisAlignedBB?

  fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB>

  fun getRaytraceBoundingBox(block: Block): AxisAlignedBB?

  fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB>

  fun getSelectedBoundingBox(block: Block, result: RayTraceResult): AxisAlignedBB?

  fun getDrops(block: Block, fortune: Int): List<Item>

  fun getPickBlock(block: Block, target: RayTraceResult, player: EntityPlayer): Item

  fun getStrongPower(block: Block, side: Facing): Int

  fun getWeakPower(block: Block, side: Facing): Int

  fun canConnectRedstone(block: Block, side: Facing): Boolean

  fun canPlaceBlockAt(world: World, pos: PositionGrid, side: Facing?): Boolean

  fun isReplacable(block: Block): Boolean

  fun isNormalBlock(): Boolean

  fun raytrace(block: Block, from: Vec3, to: Vec3): RayTraceResult?

  fun initialize(block: Block, player: EntityPlayer, hand: EnumHand, hitSide: Facing, hitVec: Vec3)

}