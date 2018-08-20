package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.ImportedValue
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockBehavior
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.MCItem
import therealfarfetchd.quacklib.api.objects.item.toItem
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

class VanillaBlockBehavior(val type: MCBlockType) : BlockBehavior {

  override fun <T : BlockDataPart> getPart(block: Block, token: PartAccessToken<T>): T {
    error("Tried to get data part '$token' from vanilla block '$block'!")
  }

  override fun <T> getImported(block: Block, value: ImportedValue<T>): T {
    error("Tried to get import '$value' from vanilla block '$block'!")
  }

  override fun onActivated(block: Block, player: EntityPlayer, hand: EnumHand, facing: Facing, hitVec: Vec3): Boolean = unsafe {
    val worldMutable = block.worldMutable ?: return@unsafe false

    type.onBlockActivated(worldMutable.toMCWorld(), block.pos.toMCVec3i(), block.toMCBlock(), player, hand, facing, hitVec.x, hitVec.y, hitVec.z)
  }

  override fun onNeighborChanged(block: Block, side: EnumFacing) {
    TODO("not implemented")
  }

  override fun onPlaced(block: Block, player: EntityPlayer, item: Item) {
    TODO("not implemented")
  }

  override fun onRemoved(block: Block) {
    TODO("not implemented")
  }

  override fun getFaceShape(self: Block, side: Facing): BlockFaceShape = unsafe {
    self.toMCBlock().getBlockFaceShape(self.world.toMCWorld(), self.pos.toMCVec3i(), side)
  }

  override fun getSoundType(block: Block, entity: Entity?): SoundType {
    TODO("not implemented")
  }

  override fun getCollisionBoundingBox(block: Block): AxisAlignedBB? = unsafe {
    block.toMCBlock().getCollisionBoundingBox(block.world.toMCWorld(), block.pos.toMCVec3i())
  }

  override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    TODO("not implemented")
  }

  override fun getRaytraceBoundingBox(block: Block): AxisAlignedBB? = unsafe {
    block.toMCBlock().getBoundingBox(block.world.toMCWorld(), block.pos.toMCVec3i())
  }

  override fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB> {
    TODO("not implemented")
  }

  override fun getDrops(block: Block, fortune: Int): List<Item> = unsafe {
    val list = NonNullList.create<MCItem>()
    type.getDrops(list, block.world.toMCWorld(), block.pos.toMCVec3i(), block.toMCBlock(), fortune)
    list.map { it.toItem() }
  }

  override fun getPickBlock(block: Block, target: RayTraceResult, player: EntityPlayer): Item {
    TODO("not implemented")
  }

  override fun getStrongPower(block: Block, side: Facing): Int {
    TODO("not implemented")
  }

  override fun getWeakPower(block: Block, side: Facing): Int {
    TODO("not implemented")
  }

  override fun canConnectRedstone(block: Block, side: Facing): Boolean {
    TODO("not implemented")
  }

  override fun canPlaceBlockAt(world: World, pos: PositionGrid, side: Facing?): Boolean {
    TODO("not implemented")
  }

  override fun isReplacable(block: Block): Boolean = unsafe {
    type.isReplaceable(block.world.toMCWorld(), block.pos.toMCVec3i())
  }

  override fun isNormalBlock(): Boolean {
    TODO("not implemented")
  }

  override fun raytrace(block: Block, from: Vec3, to: Vec3): RayTraceResult? {
    TODO("not implemented")
  }

  override fun initialize(block: Block, player: EntityPlayer, hand: EnumHand, hitSide: Facing, hitVec: Vec3) {

  }

}