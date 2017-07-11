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
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.Scheduler
import therealfarfetchd.quacklib.common.extensions.getFacing
import therealfarfetchd.quacklib.common.extensions.minus
import therealfarfetchd.quacklib.common.extensions.plus
import java.util.*

/**
 * Created by marco on 08.07.17.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
open class QBContainer(rl: ResourceLocation, internal val factory: () -> QBlock) : Block(factory.also { tempFactory = it }().material), ITileEntityProvider {

  init {
    registryName = rl
    unlocalizedName = rl.toString()

    val factory1 = factory()

    @Suppress("LeakingThis")
    if (factory1 is IQBlockMultipart && this !is QBContainerMultipart)
      QuackLib.Logger.log(Level.WARN, "Using a multipart-enabled QBlock ($factory1) in a non-multipart Block! This means you won't get any multipart capabilities.")
  }

  internal open fun checkValid(world: IBlockAccess, pos: BlockPos) = world.getBlockState(pos).block == this && world.getTileEntity(pos) is QBContainerTile

  internal open fun requireValid(world: IBlockAccess, pos: BlockPos) {
    val block = world.getBlockState(pos).block
    check(block == this, { "Block at $pos is not $this, but $block!" })
    val tileEntity = world.getTileEntity(pos)
    check(tileEntity != null, { "There is no tile entity at $pos!" })
    check(tileEntity is QBContainerTile, { "Tile entity at $pos is not a QBContainerTile, but ${tileEntity!!::class}!" })
  }

  @Suppress("USELESS_ELVIS")
  internal open fun tempQB(world: World?, pos: BlockPos?): QBlock = (factory ?: tempFactory)().also { qb -> world?.also { qb.world = it }; pos?.also { qb.pos = it } }

  internal open fun getQBlockAt(world: IBlockAccess, pos: BlockPos): QBlock {
    requireValid(world, pos)
    val te = world.getTileEntity(pos) as QBContainerTile
    return te.qb
  }

  override fun createBlockState(): BlockStateContainer {
    @Suppress("USELESS_ELVIS")
    val qb = (factory ?: tempFactory)()
    return ExtendedBlockState(this, qb.properties.toTypedArray(), qb.unlistedProperties.toTypedArray())
  }

  override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    try {
      val block = getQBlockAt(world, pos)
      return block.applyProperties(state)
    } catch(e: IllegalStateException) {
      // probably client is still loading
      return state
    }
  }

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    try {
      val block = getQBlockAt(world, pos)
      if (state is IExtendedBlockState) return block.applyExtendedProperties(state)
    } catch(e: IllegalStateException) {
      // probably client is still loading
    }
    return state
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
    return getQBlockAt(world, pos).hardness
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
    getQBlockAt(world, pos).onPlaced(placer, stack, sidePlaced, placedX, placedY, placedZ)
    sidePlaced = EnumFacing.DOWN
    placedX = 0.0f
    placedY = 0.0f
    placedZ = 0.0f
  }

  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    brokenQBlock = (world.getTileEntity(pos) as QBContainerTile).qb
    super.breakBlock(world, pos, state)
  }

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState?, fortune: Int) {
    drops.addAll(brokenQBlock.getDroppedItems())
  }

  override fun getSelectedBoundingBox(state: IBlockState?, world: World, pos: BlockPos): AxisAlignedBB = getQBlockAt(world, pos).selectionBox + pos

  override fun getCollisionBoundingBox(blockState: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    return try {
      getQBlockAt(world, pos)
    } catch(e: IllegalStateException) {
      tempQB(world as World, pos)
    }.collisionBox
  }

  override fun getBoundingBox(state: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    if (checkValid(world, pos)) {
      return getQBlockAt(world, pos).rayCollisionBox
    } else if (checkValid(world, pos.down())) {
      // net.minecraft.client.renderer.entity.Render.renderShadowSingle calls this method but with an offset position >.<
      return getQBlockAt(world, pos.down()).rayCollisionBox
    } else throw IllegalArgumentException("There's no QBlock at $pos!")
  }

  override fun isFullCube(state: IBlockState?): Boolean = tempQB(null, null).isFullBlock

  override fun isOpaqueCube(state: IBlockState?): Boolean = tempQB(null, null).isOpaque

  override fun onBlockAdded(world: World, pos: BlockPos, state: IBlockState?) {
    Scheduler.schedule(1) {
      getQBlockAt(world, pos).onAdded()
    }
  }

  override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing): Boolean {
    val qb = getQBlockAt(world, pos)
    return qb.rotateBlock(axis)
  }

  override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState?, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return getQBlockAt(world, pos).onActivated(player, hand, facing, hitX, hitY, hitZ)
  }

  override fun neighborChanged(state: IBlockState?, world: World, pos: BlockPos, blockIn: Block?, fromPos: BlockPos) {
    getQBlockAt(world, pos).onNeighborChanged((fromPos - pos).getFacing())
  }

  companion object {
    internal lateinit var tempFactory: () -> QBlock
    internal lateinit var brokenQBlock: QBlock
    internal var sidePlaced: EnumFacing = EnumFacing.DOWN
    internal var placedX: Float = 0.0f
    internal var placedY: Float = 0.0f
    internal var placedZ: Float = 0.0f
  }

}