package therealfarfetchd.quacklib.objects.block

import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.Vec3i
import therealfarfetchd.math.getDistance
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3d
import therealfarfetchd.quacklib.api.core.extensions.toMCVec3i
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockBehavior
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.getComponentsOfType
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.orEmpty
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.block.data.PartAccessTokenImpl
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class StandardBlockBehavior(val type: BlockType) : BlockBehavior {

  val cActivate = getComponentsOfType<BlockComponentActivation>()
  val cDrops = getComponentsOfType<BlockComponentDrops>()
  val cPickBlock = getComponentsOfType<BlockComponentPickBlock>()
  val cCollision = getComponentsOfType<BlockComponentCollision>()
  val cMouseOver = getComponentsOfType<BlockComponentMouseOver>()
  val cCustomMouseOver = getComponentsOfType<BlockComponentCustomMouseOver>()
  val cNeighborListener = getComponentsOfType<BlockComponentNeighborListener>()
  val cPlacementCheck = getComponentsOfType<BlockComponentPlacementCheck>()
  val cRedstone = getComponentsOfType<BlockComponentRedstone>()
  val cData = getComponentsOfType<BlockComponentData<*>>()
  val cPlacement = getComponentsOfType<BlockComponentPlacement<BlockDataPart>>()

  private fun getContainer(block: Block) =
    unsafe { (block.mcTile as?TileQuackLib)?.c }

  override fun <T : BlockDataPart> getPart(block: Block, token: PartAccessToken<T>): T {
    if (token !is PartAccessTokenImpl<T>) error("Invalid token: $token")
    // TODO implement blocktype equality check, don't rely just on equal resourcelocation
    return getContainer(block)?.parts?.get(token.rl) as? T
           ?: error("Could not access part ${token.rl} for block ${block.type}")
  }

  override fun <T> getImported(block: Block, value: ImportedValue<T>): T {
    return value.retrieve(block)
  }

  override fun onActivated(block: Block, player: EntityPlayer, hand: EnumHand, facing: Facing, hitVec: Vec3): Boolean {
    return cActivate.any { it.onActivated(block, player, hand, facing, hitVec) }
  }

  override fun onNeighborChanged(block: Block, side: EnumFacing) {
    cNeighborListener.forEach { it.onNeighborChanged(block, side) }
  }

  override fun onPlaced(block: Block, player: EntityPlayer, item: Item) {
    // TODO
  }

  override fun getFaceShape(self: Block, side: Facing): BlockFaceShape {
    return BlockFaceShape.UNDEFINED
  }

  override fun getSoundType(block: Block, entity: Entity?): SoundType {
    return type.soundType
  }

  override fun getCollisionBoundingBox(block: Block): AxisAlignedBB? {
    return getCollisionBoundingBoxes(block)
      .takeIf { it.isNotEmpty() }
      ?.reduce(AxisAlignedBB::union)
  }

  override fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB> {
    return if (cCollision.isNotEmpty()) cCollision.flatMap { it.getCollisionBoundingBoxes(block) }
    else listOf(MCBlockType.FULL_BLOCK_AABB)
  }

  override fun getRaytraceBoundingBox(block: Block): AxisAlignedBB? {
    return getRaytraceBoundingBoxes(block)
      .takeIf { it.isNotEmpty() }
      ?.reduce(AxisAlignedBB::union)
  }

  override fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB> {
    return if (cMouseOver.isNotEmpty()) cMouseOver.flatMap { it.getRaytraceBoundingBoxes(block) }
    else listOf(MCBlockType.FULL_BLOCK_AABB)
  }

  override fun getDrops(block: Block, fortune: Int): List<Item> {
    return cDrops.flatMap { it.getDrops(block) }
  }

  override fun getPickBlock(block: Block, target: RayTraceResult, player: EntityPlayer): Item {
    return cPickBlock.firstOrNull()?.getPickBlock(block).orEmpty()
  }

  override fun isReplacable(block: Block): Boolean {
    return false
  }

  override fun raytrace(block: Block, from: Vec3, to: Vec3): RayTraceResult? {
    val boxes = getRaytraceBoundingBoxes(block)

    return (boxes.map { raytraceDo(block.pos, from, to, it) } + cCustomMouseOver.map { it.raytrace(block, from, to) })
      .asSequence()
      .filterNotNull()
      .sortedBy { getDistance(from, it.hitVec.toVec3()) }
      .firstOrNull()
  }

  private fun raytraceDo(pos: Vec3i, from: Vec3, to: Vec3, bb: AxisAlignedBB): RayTraceResult? {
    val vec3d = from - pos
    val vec3d1 = to - pos
    val raytraceresult = bb.calculateIntercept(vec3d.toMCVec3d(), vec3d1.toMCVec3d())
    return if (raytraceresult == null) null else RayTraceResult((raytraceresult.hitVec.toVec3() + pos).toMCVec3d(), raytraceresult.sideHit, pos.toMCVec3i())
  }

  override fun getStrongPower(block: Block, side: Facing): Int {
    return cRedstone.map { it.strongPowerLevel(block, side) }.max() ?: 0
  }

  override fun getWeakPower(block: Block, side: Facing): Int {
    return cRedstone.map { it.weakPowerLevel(block, side) }.max() ?: 0
  }

  override fun canConnectRedstone(block: Block, side: Facing): Boolean {
    return cRedstone.any { it.canConnectRedstone(block, side) }
  }

  override fun canPlaceBlockAt(world: World, pos: PositionGrid, side: Facing?): Boolean {
    return cPlacementCheck.all { it.canPlaceBlockAt(world, pos, side) }
  }

  override fun isNormalBlock(): Boolean {
    return cCollision.isEmpty()
  }

  override fun initialize(block: Block, player: EntityPlayer, hand: EnumHand, hitSide: Facing, hitVec: Vec3) {
    cPlacement.forEach { it.initialize(block, player, hand, hitSide, hitVec) }
  }

  override fun copy(block: Block): Block {
    TODO("not implemented")
  }

  private inline fun <reified T : BlockComponent> getComponentsOfType() =
    type.getComponentsOfType<T>()

}