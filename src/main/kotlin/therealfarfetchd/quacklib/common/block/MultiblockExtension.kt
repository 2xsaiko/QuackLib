package therealfarfetchd.quacklib.common.block

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.extensions.minus
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultiblock
import therealfarfetchd.quacklib.common.api.qblock.QBContainer
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.util.QNBTCompound
import java.util.*
import net.minecraft.block.Block as MCBlock
import net.minecraft.tileentity.TileEntity as MCTile

object MultiblockExtension {
  @Suppress("OverridingDeprecatedMember")
  object Block : MCBlock(Material.ROCK), ITileEntityProvider {
    init {
      setRegistryName(ModID, "multiblock1")
      setLightOpacity(0)
    }

    override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
      val qb = getMainBlockM(world, pos)
      if (qb == null) {
        world.setBlockToAir(pos)
        return true
      }
      return qb.onActivatedRemote(player, hand, facing, pos, hitX, hitY, hitZ)
    }

    override fun getBlockHardness(blockState: IBlockState?, world: World, pos: BlockPos): Float {
      val mpos = getMainBlockPos(world, pos)
      if (mpos == pos) return -1.0f
      return world.getBlockState(mpos).getBlockHardness(world, mpos)
    }

    override fun getPlayerRelativeBlockHardness(state: IBlockState, player: EntityPlayer, world: World, pos: BlockPos): Float {
      val mpos = getMainBlockPos(world, pos)
      if (mpos == pos) return 0.0f
      return world.getBlockState(mpos).getPlayerRelativeBlockHardness(player, world, mpos)
    }

    override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer): Boolean {
      val mpos = getMainBlockPos(world, pos)
      return world.getBlockState(mpos).block.canHarvestBlock(world, mpos, player)
    }

    override fun removedByPlayer(state: IBlockState, world: World, pos: BlockPos, player: EntityPlayer, willHarvest: Boolean): Boolean {
      val mpos = getMainBlockPos(world, pos)
      val b = getMainBlockM(world, pos)
              ?: return super.removedByPlayer(state, world, pos, player, willHarvest)
      b.onRemoteBreak(pos - mpos, player)
      return false
    }

    override fun isOpaqueCube(state: IBlockState?) = false
    override fun isFullCube(state: IBlockState?) = false
    override fun getRenderType(state: IBlockState?) = EnumBlockRenderType.INVISIBLE
    override fun isTopSolid(state: IBlockState?) = false
    override fun getBlockFaceShape(worldIn: IBlockAccess?, state: IBlockState?, pos: BlockPos?, face: EnumFacing?) = BlockFaceShape.UNDEFINED
    override fun getMobilityFlag(state: IBlockState?) = EnumPushReaction.BLOCK
    override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int) = null
    override fun createNewTileEntity(worldIn: World?, meta: Int) = Tile()
  }

  class Tile : MCTile() {
    internal var rootOffset: BlockPos = BlockPos.ORIGIN

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
      super.writeToNBT(nbt)
      with(QNBTCompound(nbt)) {
        ushort["xO"] = rootOffset.x
        ushort["yO"] = rootOffset.y
        ushort["zO"] = rootOffset.z
      }
      return nbt
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
      super.readFromNBT(nbt)
      with(QNBTCompound(nbt)) {
        rootOffset = BlockPos(
          short["xO"].toInt(),
          short["yO"].toInt(),
          short["zO"].toInt()
        )
      }
    }

    override fun getUpdateTag() = writeToNBT(NBTTagCompound())

    override fun getUpdatePacket(): SPacketUpdateTileEntity? {
      return SPacketUpdateTileEntity(getPos(), 0, updateTag)
    }

    override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
      readFromNBT(pkt.nbtCompound)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
      return super.hasCapability(capability, facing)
    }

    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
      return super.getCapability(capability, facing)
    }
  }

  fun getMainBlockPos(world: IBlockAccess, pos: BlockPos) =
    (world.getTileEntity(pos) as? Tile)!!.rootOffset + pos

  fun getMainBlock(world: IBlockAccess, pos: BlockPos): QBlock? {
    val mpos = getMainBlockPos(world, pos)
    return if (world.getBlockState(mpos).block is QBContainer) world.getQBlock(mpos)
    else null
  }

  fun getMainBlockM(world: IBlockAccess, pos: BlockPos) =
    getMainBlock(world, pos) as? IQBlockMultiblock
}