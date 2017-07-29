package therealfarfetchd.quacklib.common.qblock

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.Explosion
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.extensions.getFacing
import therealfarfetchd.quacklib.common.extensions.getQBlock
import therealfarfetchd.quacklib.common.extensions.minus
import therealfarfetchd.quacklib.common.extensions.plus
import java.util.*

/**
 * Created by marco on 08.07.17.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
open class QBContainer(rl: ResourceLocation, factory: () -> QBlock) : Block(factory.also { tempFactory = it }().material), ITileEntityProvider {

  private var _factory: (() -> QBlock)? = null
  internal val factory: () -> QBlock
    get() = _factory ?: tempFactory

  init {
    _factory = tempFactory

    registryName = rl
    unlocalizedName = rl.toString()

    val factory1 = factory()

    @Suppress("LeakingThis")
    if (factory1 is IQBlockMultipart && this !is QBContainerMultipart)
      QuackLib.Logger.log(Level.WARN, "Using a multipart-enabled QBlock ($factory1) in a non-multipart container! This means you won't get any multipart capabilities.")
  }

  protected fun noqb(pos: BlockPos? = null): Nothing = error("No QBlock${if (pos != null) " at $pos" else ""}.")

  internal open fun tempQB(world: World?, pos: BlockPos?): QBlock = factory().also { qb -> world?.also { qb.world = it }; pos?.also { qb.pos = it } }

  override fun createBlockState(): BlockStateContainer {
    val qb = factory()
    return ExtendedBlockState(this, qb.properties.toTypedArray(), qb.unlistedProperties.toTypedArray())
  }

  override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    return world.getQBlock(pos)?.applyProperties(state) ?: state
  }

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    return (state as? IExtendedBlockState)?.also { world.getQBlock(pos)?.applyExtendedProperties(it) } ?: state
  }

  override fun getStateForPlacement(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
    placedX = hitX
    placedY = hitY
    placedZ = hitZ
    sidePlaced = facing.opposite
    when (sidePlaced) {
      EnumFacing.DOWN -> placedY = 0.0f
      EnumFacing.UP -> placedY = 1.0f
      EnumFacing.NORTH -> placedZ = 0.0f
      EnumFacing.SOUTH -> placedZ = 1.0f
      EnumFacing.WEST -> placedX = 0.0f
      EnumFacing.EAST -> placedX = 1.0f
    }
    return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
  }

  override fun getMetaFromState(state: IBlockState?): Int = 0
  override fun getStateFromMeta(meta: Int): IBlockState = defaultState

  override fun getBlockHardness(blockState: IBlockState?, world: World, pos: BlockPos): Float {
    return world.getQBlock(pos)?.hardness ?: noqb(pos)
  }

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = factory()
    if (qb is ITickable) return QBContainerTile.Ticking(qb)
    else return QBContainerTile(qb)
  }

  override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean = tempQB(world, pos).canStay()

  override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
    return tempQB(world, pos).canBePlacedOnSide(side)
  }

  override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack) {
    world.getQBlock(pos)?.onPlaced(placer, stack, sidePlaced, placedX, placedY, placedZ)
    sidePlaced = EnumFacing.DOWN
    placedX = 0.0f
    placedY = 0.0f
    placedZ = 0.0f
  }

  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    brokenQBlock = (world.getTileEntity(pos) as QBContainerTile).qb
    brokenQBlock.onBreakBlock()
    super.breakBlock(world, pos, state)
  }

  override fun onBlockExploded(world: World, pos: BlockPos, explosion: Explosion?) {
    super.onBlockExploded(world, pos, explosion)
  }

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState?, fortune: Int) {
    val qb = (world.getTileEntity(pos) as? QBContainerTile)?.qb ?: brokenQBlock
    drops.addAll(qb.getDroppedItems())
  }

  override fun getSelectedBoundingBox(state: IBlockState?, world: World, pos: BlockPos): AxisAlignedBB {
    return (world.getQBlock(pos)?.selectionBox ?: noqb(pos)) + pos
  }

  override fun getCollisionBoundingBox(blockState: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    return (world.getQBlock(pos) ?: tempQB(world as? World, pos)).collisionBox
  }

  override fun getBoundingBox(state: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    // net.minecraft.client.renderer.entity.Render.renderShadowSingle calls this function but with an offset position >.<
    return (world.getQBlock(pos) ?: world.getQBlock(pos.down()))?.rayCollisionBox ?: FULL_BLOCK_AABB
  }

  override fun isFullCube(state: IBlockState?): Boolean = tempQB(null, null).isFullBlock

  override fun isOpaqueCube(state: IBlockState?): Boolean = tempQB(null, null).isOpaque

  override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
    return world.getQBlock(pos)?.rotateBlock(axis) ?: false
  }

  override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState?, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return world.getQBlock(pos)?.onActivated(player, hand, facing, hitX, hitY, hitZ) ?: false
  }

  override fun neighborChanged(state: IBlockState?, world: World, pos: BlockPos, blockIn: Block?, fromPos: BlockPos) {
    world.getQBlock(pos)?.onNeighborChanged((fromPos - pos).getFacing())
  }

  companion object {
    private lateinit var tempFactory: () -> QBlock
    internal lateinit var brokenQBlock: QBlock
    internal var sidePlaced: EnumFacing = EnumFacing.DOWN
    internal var placedX: Float = 0.0f
    internal var placedY: Float = 0.0f
    internal var placedZ: Float = 0.0f
  }

}