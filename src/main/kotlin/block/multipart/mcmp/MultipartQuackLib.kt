package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.container.IPartInfo
import mcmultipart.api.multipart.IMultipart
import mcmultipart.api.multipart.IMultipartTile
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentMultipart
import therealfarfetchd.quacklib.api.block.component.BlockComponentOcclusion
import therealfarfetchd.quacklib.block.data.BlockDataDirectRef
import therealfarfetchd.quacklib.block.data.BlockDataROImpl
import therealfarfetchd.quacklib.block.impl.BlockQuackLib
import therealfarfetchd.quacklib.block.impl.DataContainer
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class MultipartQuackLib(private val block: BlockQuackLib) : IMultipart {

  val cMultipart = block.getComponentsOfType<BlockComponentMultipart>().first()
  val cOcclusion = block.getComponentsOfType<BlockComponentOcclusion>()

  override fun getBlock(): BlockQuackLib = block

  override fun getSlotFromWorld(world: IBlockAccess, pos: BlockPos, state: IBlockState): IPartSlot {
    val data = BlockDataROImpl(world, pos, state)
    return MultipartAPIImpl.slotMap.getValue(cMultipart.getSlot(data))
  }

  override fun getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase): IPartSlot {
    error("Use custom getSlotForPlacement instead!")
  }

  fun getSlotForPlacement(world: World, pos: BlockPos, state: IBlockState, facing: EnumFacing, hitVec: Vec3, player: EntityPlayer, c: DataContainer): IPartSlot {
    val data = BlockDataDirectRef(c, world, pos, state)
    return MultipartAPIImpl.slotMap.getValue(cMultipart.getSlot(data))
  }

  override fun getGhostSlots(part: IPartInfo): Set<IPartSlot> {
    return cMultipart.getExtraSlots(getBlockData(part))
      .map { MultipartAPIImpl.slotMap.getValue(it) }
      .toSet()
  }

  override fun getOcclusionBoxes(part: IPartInfo): List<AxisAlignedBB> {
    val data = getBlockData(part)

    return if (cOcclusion.isNotEmpty()) {
      cOcclusion
        .flatMap { it.getOcclusionBoundingBoxes(data) }
        .also { if (it.isEmpty()) return emptyList() }
    } else listOf(Block.FULL_BLOCK_AABB)
  }

  override fun createMultipartTile(world: World, slot: IPartSlot, state: IBlockState): IMultipartTile {
    val te = super.createMultipartTile(world, slot, state)
    data.get()?.also { (te.tileEntity as TileQuackLib).c.import(it) }
    data.set(null)
    return te
  }

  fun getBlockData(part: IPartInfo): BlockDataDirectRef {
    return BlockDataDirectRef((part.tile.tileEntity as TileQuackLib).c, part.partWorld, part.partPos, part.state)
  }

  companion object {
    // Used to set data when placing block.
    val data = ThreadLocal<DataContainer?>()
  }

}