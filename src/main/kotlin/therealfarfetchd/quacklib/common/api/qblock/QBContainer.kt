package therealfarfetchd.quacklib.common.api.qblock

import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.SoundType
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.IBlockWrenchable
import therealfarfetchd.quacklib.common.api.block.IBlockAdvancedOutline
import therealfarfetchd.quacklib.common.api.extensions.*
import therealfarfetchd.quacklib.common.api.util.ClientServerSeparateData
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by marco on 08.07.17.
 */
@Suppress("OverridingDeprecatedMember", "DEPRECATION")
open class QBContainer(factory: () -> QBlock) : Block(factory.also { tempFactory = it }().material), ITileEntityProvider, IBlockAdvancedOutline, IBlockWrenchable {
  private var _factory: (() -> QBlock)? = null
  internal val factory: () -> QBlock
    get() = _factory ?: tempFactory

  private val isRedstone: Boolean by lazy { tempQB(null, null) is IQBlockRedstone }
  private val _useNeighborBrightness by lazy { tempQB(null, null).useNeighborBrightness }
  private val _isFullBlock by lazy { tempQB(null, null).isFullBlock }
  private val _isOpaque by lazy { tempQB(null, null).isOpaque }
  private val _isTopSolid by lazy { tempQB(null, null).isTopSolid }
  private val _canRenderInLayer by lazy { tempQB(null, null)::canRenderInLayer }

  private var initDone = false

  init {
    _factory = tempFactory

    val factory1 = factory()

    @Suppress("LeakingThis")
    if (factory1 is IQBlockMultipart && this !is QBContainerMultipart)
      QuackLib.Logger.log(Level.WARN, "Using a multipart-enabled QBlock ($factory1) in a non-multipart container! This means you won't get any multipart capabilities.")
    initDone = true
  }

  protected fun checkQBType(vararg types: KClass<*>) {
    val value = factory()
    types
      .filterNot { it.isInstance(value) }
      .takeIf { it.isNotEmpty() }
      ?.apply { error("QBlock ${value::class} is not a subclass of ${joinToString { it.qualifiedName.orEmpty() }}") }
  }

  protected fun noqb(pos: BlockPos? = null): Nothing = error("No QBlock${if (pos != null) " at $pos" else ""}.")

  internal open fun tempQB(world: World?, pos: BlockPos?): QBlock = factory().also { qb -> world?.also { qb.world = it }; pos?.also { qb.pos = it }; qb.prePlaced = true }

  override fun createBlockState(): BlockStateContainer {
    val qb = factory()
    return when {
      qb.unlistedProperties.isEmpty() -> BlockStateContainer(this, *qb.properties.toTypedArray())
      else                            -> ExtendedBlockState(this, qb.properties.toTypedArray(), qb.unlistedProperties.toTypedArray())
    }
  }

  override fun canRenderInLayer(state: IBlockState?, layer: BlockRenderLayer) = _canRenderInLayer(layer)

  override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos) =
    world.getQBlock(pos)?.applyProperties(state) ?: state

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos) =
    (state as? IExtendedBlockState)?.let { world.getQBlock(pos)?.applyExtendedProperties(it) } ?: state

  override fun getStateForPlacement(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
    placedX = hitX
    placedY = hitY
    placedZ = hitZ
    sidePlaced = facing.opposite
    when (sidePlaced) {
      EnumFacing.DOWN  -> if (placedY == 1.0f) placedY = 0.0f
      EnumFacing.UP    -> if (placedY == 0.0f) placedY = 1.0f
      EnumFacing.NORTH -> if (placedZ == 1.0f) placedZ = 0.0f
      EnumFacing.SOUTH -> if (placedZ == 0.0f) placedZ = 1.0f
      EnumFacing.WEST  -> if (placedX == 1.0f) placedX = 0.0f
      EnumFacing.EAST  -> if (placedX == 0.0f) placedX = 1.0f
    }
    return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
  }

  override fun getMetaFromState(state: IBlockState?): Int = 0
  override fun getStateFromMeta(meta: Int): IBlockState = defaultState

  override fun getBlockHardness(blockState: IBlockState?, world: World, pos: BlockPos) =
    world.getQBlock(pos)?.hardness ?: noqb(pos)

  override fun canHarvestBlock(world: IBlockAccess, pos: BlockPos, player: EntityPlayer) =
    world.getQBlock(pos)?.canHarvestBlock(player, world, pos) ?: noqb(pos)

  override fun getPlayerRelativeBlockHardness(state: IBlockState?, player: EntityPlayer, world: World, pos: BlockPos) =
    world.getQBlock(pos)?.blockStrength(player) ?: noqb(pos)

  override fun getSoundType(state: IBlockState?, world: World, pos: BlockPos, entity: Entity?) =
    world.getQBlock(pos)?.soundType ?: SoundType.STONE

  override fun getUseNeighborBrightness(state: IBlockState?): Boolean = _useNeighborBrightness

  override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity {
    val qb = factory()
    (worldIn ?: savedWorld)?.also { qb.world = it }
    savedPos?.also { qb.pos = it }
    if (savedNbt != null) {
      qb.prePlaced = true
      qb.loadData(savedNbt!!, DataTarget.Save)
      qb.prePlaced = false
    }
    return WrapperImplManager.createTileEntity(qb::class).invoke(qb)
  }

  override fun canPlaceBlockAt(world: World, pos: BlockPos) =
    buildPart(world, pos) { canStay() }

  override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing) =
    buildPart(world, pos) { canBePlacedOnSide(side.opposite) }

  override fun onBlockPlacedBy(world: World, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack) {
    val qBlock = world.getQBlock(pos)
    qBlock?.onPlaced(placer, stack, sidePlaced, placedX, placedY, placedZ)
    sidePlaced = EnumFacing.DOWN
    placedX = 0.0f
    placedY = 0.0f
    placedZ = 0.0f
  }

  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun breakBlock(world: World, pos: BlockPos, state: IBlockState?) {
    val qb = (world.getTileEntity(pos) as QBContainerTile).qb
    qb.onBreakBlock()
    brokenQBlock += world to pos to qb
    super.breakBlock(world, pos, state)
  }

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState?, fortune: Int) {
    val qb = (world.getTileEntity(pos) as? QBContainerTile)?.qb ?: brokenQBlock[world to pos]!!
    drops.addAll(qb.getDroppedItems())
  }

  override fun canProvidePower(state: IBlockState?): Boolean = isRedstone

  override fun canConnectRedstone(state: IBlockState?, world: IBlockAccess, pos: BlockPos, side: EnumFacing?): Boolean {
    if (side != null) {
      return (world.getQBlock(pos) as? IQBlockRedstone)?.canConnect(side) ?: false
    }
    return false
  }

  override fun getWeakPower(blockState: IBlockState?, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing?): Int {
    if (side != null) {
      return (blockAccess.getQBlock(pos) as? IQBlockRedstone)?.getOutput(side, false) ?: 0
    }
    return 0
  }

  override fun getStrongPower(blockState: IBlockState?, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing?): Int {
    if (side != null) {
      return (blockAccess.getQBlock(pos) as? IQBlockRedstone)?.getOutput(side, true) ?: 0
    }
    return 0
  }

  override fun getOutlineBoxes(world: World, pos: BlockPos, state: IBlockState) =
    world.getQBlock(pos)?.selectionBox ?: noqb(pos)

  override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos) =
    getOutlineBoxes(world, pos, state).reduceOrNull(AxisAlignedBB::union)?.let { it + pos }

  override fun getCollisionBoundingBox(blockState: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    val qb = world.getQBlock(pos)
    return when {
      qb != null     -> qb.collisionBox
      world is World -> buildPart(world, pos) {
        collisionBox
      }
      else           -> error("This should never happen! Raise hell if it does…")
    }.reduceOrNull(AxisAlignedBB::union)
  }

  override fun addCollisionBoxToList(state: IBlockState?, world: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, p_185477_7_: Boolean) {
    val qb = world.getQBlock(pos)
    when {
      qb != null -> qb.collisionBox
      else       -> buildPart(world, pos) {
        collisionBox
      }
    }
      .map { it + pos }
      .filter(entityBox::intersects)
      .also { collidingBoxes.addAll(it) }
  }

  override fun getBoundingBox(state: IBlockState?, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    // net.minecraft.client.renderer.entity.Render.renderShadowSingle calls this function but with an offset position >.<
    return (world.getQBlock(pos) ?: world.getQBlock(pos.down()))?.rayCollisionBox ?: FULL_BLOCK_AABB
  }

  override fun isFullCube(state: IBlockState?) = _isFullBlock

  override fun isOpaqueCube(state: IBlockState?) =
    if (initDone) _isOpaque else tempQB(null, null).isOpaque

  override fun doesSideBlockRendering(state: IBlockState?, world: IBlockAccess, pos: BlockPos, face: EnumFacing) =
    (world.getQBlock(pos) ?: world.getQBlock(pos.down()))?.isSideOpaque(face)
    ?: super.doesSideBlockRendering(state, world, pos, face)

  override fun getLightValue(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?) =
    super.getLightValue(state, world, pos) // TODO

  override fun getBlockFaceShape(world: IBlockAccess, state: IBlockState?, pos: BlockPos, facing: EnumFacing) =
    world.getQBlock(pos)?.getBlockFaceShape(facing) ?: noqb(pos)

  override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing) =
    world.getQBlock(pos)?.rotateBlock(axis) ?: false

  override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing, player: EntityPlayer?, hitX: Float, hitY: Float, hitZ: Float) =
    world.getQBlock(pos)?.rotateBlock(axis, player, hitX, hitY, hitZ) ?: false

  override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState?, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) =
    world.getQBlock(pos)?.onActivated(player, hand, facing, hitX, hitY, hitZ) ?: false

  override fun neighborChanged(state: IBlockState?, world: World, pos: BlockPos, blockIn: Block?, fromPos: BlockPos) {
    world.getQBlock(pos)?.onNeighborChanged((fromPos - pos).getFacing())
  }

  override fun onNeighborChange(world: IBlockAccess, pos: BlockPos, neighbor: BlockPos) {
    world.getQBlock(pos)?.onNeighborTEChanged((neighbor - pos).getFacing())
  }

  override fun getPickBlock(state: IBlockState?, target: RayTraceResult?, world: World?, pos: BlockPos?, player: EntityPlayer?) =
    super.getPickBlock(state, target, world, pos, player) // TODO

  override fun getItem(world: World, pos: BlockPos, state: IBlockState?) =
    world.getQBlock(pos)?.getItem() ?: noqb(pos)

  override fun isTopSolid(state: IBlockState?) = _isTopSolid

  protected fun <T> buildPart(world: World? = null, pos: BlockPos? = null, op: QBlock.() -> T): T {
    world?.also { savedWorld = it }
    pos?.also { savedPos = it }
    val myqb = tempQB(savedWorld, savedPos)
    if (savedNbt != null) myqb.loadData(savedNbt!!, DataTarget.Save)
    val ret = op(myqb)
    myqb.saveData(QNBTCompound().also { savedNbt = it }, DataTarget.Save)
    return ret
  }

  companion object {
    private lateinit var tempFactory: () -> QBlock
    internal var brokenQBlock: Map<Pair<IBlockAccess, BlockPos>, QBlock> = emptyMap()
    internal var sidePlaced: EnumFacing = EnumFacing.DOWN
    internal var placedX: Float = 0.0f
    internal var placedY: Float = 0.0f
    internal var placedZ: Float = 0.0f

    internal var savedPos: BlockPos? by ClientServerSeparateData { null }
    internal var savedWorld: World? by ClientServerSeparateData { null }
    internal var savedNbt: QNBTCompound? by ClientServerSeparateData { null }
  }
}