package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.IMultipartTile
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.Block.spawnAsEntity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentMultipart
import therealfarfetchd.quacklib.api.block.component.BlockComponentOcclusion
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.MCBlock
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.world.MCWorld
import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.block.impl.TileQuackLib
import therealfarfetchd.quacklib.objects.block.BlockImpl
import therealfarfetchd.quacklib.objects.world.toWorld

class MultipartQuackLib(private val block: BlockQuackLib) : IMultipart {

  val cMultipart = block.getComponentsOfType<BlockComponentMultipart>().first()
  val cOcclusion = block.getComponentsOfType<BlockComponentOcclusion>()

  override fun getBlock(): BlockQuackLib = block

  override fun getSlotFromWorld(world: MCWorld, pos: BlockPos, state: MCBlock): IPartSlot {
    return MultipartAPIImpl.slotMap.getValue(cMultipart.getSlot(block.getBlockImpl(world, pos, state)))
  }

  override fun getSlotForPlacement(world: MCWorldMutable, pos: BlockPos, state: MCBlock, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase): IPartSlot {
    error("Use custom getSlotForPlacement instead!")
  }

  fun getSlotForPlacement(facing: EnumFacing, hitVec: Vec3, player: EntityPlayer, b: Block): IPartSlot {
    return MultipartAPIImpl.slotMap.getValue(cMultipart.getSlot(b))
  }

  override fun getGhostSlots(part: IPartInfo): Set<IPartSlot> {
    return cMultipart.getExtraSlots(getBlock(part))
      .map { MultipartAPIImpl.slotMap.getValue(it) }
      .toSet()
  }

  override fun getOcclusionBoxes(part: IPartInfo): List<AxisAlignedBB> {
    val data = getBlock(part)

    return if (cOcclusion.isNotEmpty()) {
      cOcclusion
        .flatMap { it.getOcclusionBoundingBoxes(data) }
        .also { if (it.isEmpty()) return emptyList() }
    } else listOf(MCBlockType.FULL_BLOCK_AABB)
  }

  override fun createMultipartTile(world: MCWorldMutable, slot: IPartSlot, state: MCBlock): IMultipartTile {
    val te = super.createMultipartTile(world, slot, state)
    val container = unsafe { (data.get()?.getMCTile() as? TileQuackLib)?.c }
    if (container != null)
      (te.tileEntity as TileQuackLib).c.import(container)
    data.set(null)
    return te
  }

  override fun dropPartAsItem(part: IPartInfo, fortune: Int) {
    val world = part.partWorld
    val aworld = part.actualWorld
    val pos = part.partPos

    if (aworld.isRemote || aworld.restoringBlockSnapshots) return

    val drops = part.part.getDrops(world, pos, part, fortune)
    drops.forEach { spawnAsEntity(aworld, pos, it) }
  }

  fun getBlock(part: IPartInfo): Block {
    return BlockImpl(block(part.state.block), part.partWorld.toWorld(), part.partPos.toVec3i(), part.state, part.tile.tileEntity)
  }

  override fun canPlacePartAt(world: MCWorldMutable, pos: BlockPos): Boolean {
    return block.type.behavior.canPlaceBlockAt(world.toWorld(), pos.toVec3i(), null)
  }

  override fun canPlacePartOnSide(world: MCWorldMutable, pos: BlockPos, side: EnumFacing, slot: IPartSlot): Boolean {
    return block.type.behavior.canPlaceBlockAt(world.toWorld(), pos.toVec3i(), side)
  }

  companion object {
    // Used to set data when placing block.
    val data = ThreadLocal<Block?>()
  }

}