package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import scala.Tuple5
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.getDistance
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.block.data.*
import therealfarfetchd.quacklib.block.data.render.PropertyData
import therealfarfetchd.quacklib.block.data.render.PropertyDataExtended
import java.util.*
import kotlin.reflect.jvm.jvmName

typealias SetPropertyRetrievers = Set<(IBlockState, IBlockAccess, BlockPos, TileQuackLib) -> IBlockState>
typealias SetExtendedPropertyRetrievers = Set<(IExtendedBlockState, IBlockAccess, BlockPos, TileQuackLib) -> IExtendedBlockState>
typealias MapProperties = Map<PropertyResourceLocation, PropertyData<*>>
typealias MapExtendedProperties = Map<PropertyResourceLocation, PropertyDataExtended<*>>

@Suppress("OverridingDeprecatedMember")
class BlockQuackLib(val def: BlockConfiguration) : Block(def.material.also { tempBlockConf = def }), BlockExtraDebug {

  private var initDone = false

  val needsTool = def.needsTool
  val tools = def.validTools

  val components = def.components.asReversed()

  val cActivate = getComponentsOfType<BlockComponentActivation>()
  val cDrops = getComponentsOfType<BlockComponentDrops>()
  val cPickBlock = getComponentsOfType<BlockComponentPickBlock>()
  val cCollision = getComponentsOfType<BlockComponentCollision>()
  val cMouseOver = getComponentsOfType<BlockComponentMouseOver>()
  val cCustomMouseOver = getComponentsOfType<BlockComponentCustomMouseOver>()
  val cNeighborListener = getComponentsOfType<BlockComponentNeighborListener>()
  val cPlacementCheck = getComponentsOfType<BlockComponentPlacementCheck>()
  val cData = getComponentsOfType<BlockComponentData<*>>()

  val needsTile = getComponentsOfType<BlockComponentNeedTE>().isNotEmpty()
  val needsTick = getComponentsOfType<BlockComponentTickable>().isNotEmpty()

  lateinit var propRetrievers: SetPropertyRetrievers
  lateinit var extpropRetrievers: SetExtendedPropertyRetrievers

  lateinit var properties: MapProperties
  lateinit var extproperties: MapExtendedProperties

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()
    def.hardness?.also {
      setHardness(it)
    } ?: setBlockUnbreakable()

    initDone = true
    soundType = def.soundType
    cData.forEach { it.part = PartAccessTokenImpl(it.rl) }
  }

  override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return cActivate
      .map { it.onActivated(BlockDataImpl(world, pos, state), playerIn, hand, facing, Vec3(hitX, hitY, hitZ)) }
      .any { it }
  }

  override fun createBlockState(): BlockStateContainer {
    val bs = createBlockState(this)

    properties = bs._2()
    extproperties = bs._3()
    propRetrievers = bs._4()
    extpropRetrievers = bs._5()

    return bs._1()
  }

  override fun getActualState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    val te = world.getTileEntity(pos) as? TileQuackLib ?: return state
    return propRetrievers.fold(state) { acc, op -> op(acc, world, pos, te) }
  }

  override fun getExtendedState(state: IBlockState, world: IBlockAccess, pos: BlockPos): IBlockState {
    val te = world.getTileEntity(pos) as? TileQuackLib ?: return state
    state as? IExtendedBlockState ?: return state
    return extpropRetrievers.fold(state) { acc, op -> op(acc, world, pos, te) }
  }

  // ray trace
  override fun collisionRayTrace(state: IBlockState, world: World, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
    val data = BlockDataImpl(world, pos, state)

    val startv = start.toVec3()
    val endv = end.toVec3()

    val boxes = if (cMouseOver.isNotEmpty()) {
      cMouseOver
        .flatMap { it.getRaytraceBoundingBoxes(data) }
        .takeIf { it.isNotEmpty() }
        .orEmpty()
    } else listOf(FULL_BLOCK_AABB)

    return (boxes.map { rayTrace(pos, start, end, it) } + cCustomMouseOver.map { it.raytrace(data, startv, endv) })
      .asSequence()
      .filterNotNull()
      .sortedBy { getDistance(startv, it.hitVec.toVec3()) }
      .firstOrNull()
  }

  // ray trace
  override fun getBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB {
    val data = BlockDataROImpl(world, pos, state)

    return if (cMouseOver.isNotEmpty()) {
      cMouseOver
        .flatMap { it.getRaytraceBoundingBoxes(data) }
        .also { if (it.isEmpty()) return NOPE_AABB }
        .reduce(AxisAlignedBB::union)
    } else FULL_BLOCK_AABB
  }

  // collision
  override fun getCollisionBoundingBox(state: IBlockState, world: IBlockAccess, pos: BlockPos): AxisAlignedBB? {
    val data = BlockDataROImpl(world, pos, state)
    if (world.getTileEntity(pos) !is TileQuackLib) return null
    return getCollisionBoundingBox(data)
  }

  // collision
  fun getCollisionBoundingBox(data: BlockDataRO): AxisAlignedBB? {
    return if (cCollision.isNotEmpty()) {
      cCollision
        .flatMap { it.getCollisionBoundingBoxes(data) }
        .also { if (it.isEmpty()) return null }
        .reduce(AxisAlignedBB::union)
    } else FULL_BLOCK_AABB
  }

  // collision
  override fun addCollisionBoxToList(state: IBlockState, world: World, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
    val data = BlockDataImpl(world, pos, state)

    (cCollision.takeIf { it.isNotEmpty() }?.flatMap { it.getCollisionBoundingBoxes(data) }
     ?: setOf(FULL_BLOCK_AABB))
      .map { it.offset(pos) }
      .filter(entityBox::intersects)
      .also { collidingBoxes.addAll(it) }
  }

  // outline
  override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
    // TODO
    return super.getSelectedBoundingBox(state, world, pos)
  }

  override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos) {
    val facing = fromPos.subtract(pos).let { EnumFacing.getFacingFromVector(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()) }
    val data = BlockDataImpl(world, pos, state)
    cNeighborListener.forEach { it.onNeighborChanged(data, facing) }
  }

  override fun canPlaceBlockOnSide(world: World, pos: BlockPos, side: EnumFacing): Boolean {
    return super.canPlaceBlockAt(world, pos) &&
           cPlacementCheck.fold(true) { b, comp -> b && comp.canPlaceBlockAt(world, pos, side) }
  }

  override fun canPlaceBlockAt(world: World, pos: BlockPos): Boolean {
    return super.canPlaceBlockAt(world, pos) &&
           cPlacementCheck.fold(true) { b, comp -> b && comp.canPlaceBlockAt(world, pos, null) }
  }

  @Suppress("UNCHECKED_CAST")
  override fun addInformation(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {
    val state = getExtendedState(state, world, pos)

    if (state is IExtendedBlockState) {
      right += state.unlistedProperties
        .asSequence()
        .filter { it.value.isPresent }
        .map { Pair(it.key as IUnlistedProperty<Any>, it.value.get()) }
        .map { "§7${it.first.name}: ${it.first.valueToString(it.second)}" }
    }

    left += ""
    left +=
      """§b§l[Block]
        |Has TE: $needsTile${" (ticking)".takeIf { needsTick }.orEmpty()}
        |Components (${components.size}):
      """.trimMargin().lines()
    left += components.flatMap { getComponentInfo(world, pos, state, it) }
  }

  private fun getComponentInfo(world: World, pos: BlockPos, state: IBlockState, c: BlockComponent): List<String> {
    var descString = " - "
    descString += c::class.simpleName ?: c::class.qualifiedName ?: c::class.jvmName
    if (c is BlockComponentRegistered) descString += " (${c.rl})"
    if (c is BlockComponentInfo) descString += ":"

    var list = listOf(descString)
    if (c is BlockComponentInfo) {
      list += c.getInfo(BlockDataImpl(world, pos, state)).map { it.prependIndent("     ") }
    }
    return list
  }

  override fun getHarvestLevel(state: IBlockState): Int {
    if (tools.isEmpty()) return -1
    return tools.first().level
  }

  override fun isToolEffective(type: String?, state: IBlockState): Boolean {
    if (!needsTool) return true
    return type in tools.map { it.toolName }
  }

  override fun hasTileEntity(state: IBlockState): Boolean = needsTile

  override fun createTileEntity(world: World, state: IBlockState): TileQuackLib? =
    when {
      needsTick -> TileQuackLib.Tickable(def)
      needsTile -> TileQuackLib(def)
      else -> null
    }

  override fun getMetaFromState(state: IBlockState?): Int = 0

  override fun isOpaqueCube(state: IBlockState): Boolean {
    // TODO query model
    return if (initDone) cCollision.isEmpty()
    else tempBlockConf.components.none { it is BlockComponentCollision }
  }

  override fun isNormalCube(state: IBlockState): Boolean {
    return cCollision.isEmpty()
  }

  override fun isFullCube(state: IBlockState): Boolean {
    return cCollision.isEmpty()
  }

  // TODO
  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
    // super.getDrops(drops, world, pos, state, fortune)
    cDrops.forEach { drops += it.getDrops(BlockDataROImpl(world, pos, state)) }
  }

  override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
    return cPickBlock.firstOrNull()?.getPickBlock(BlockDataImpl(world, pos, state))
           ?: super.getPickBlock(state, target, world, pos, player)
  }

  inline fun <reified T : BlockComponent> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

  companion object {
    private lateinit var tempBlockConf: BlockConfiguration

    val NOPE_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    // FIXME use custom tuple here, not scalas
    fun createBlockState(block: Block?): Tuple5<BlockStateContainer, MapProperties, MapExtendedProperties, SetPropertyRetrievers, SetExtendedPropertyRetrievers> {
      val cPart = tempBlockConf.components.asReversed().mapNotNull { it as? BlockComponentData<*> }

      var properties: MapProperties = emptyMap()
      var extproperties: MapExtendedProperties = emptyMap()
      var propRetrievers: SetPropertyRetrievers = emptySet()
      var extpropRetrievers: SetExtendedPropertyRetrievers = emptySet()

      for (c in cPart) {
        val defs = c.createPart().defs
        for ((name, def) in defs) {
          if (!def.render) continue

          val prl = PropertyResourceLocation(c.rl, name)
          val needsExt = def.validValues == null || def.validValues!!.size > 32
          if (needsExt) {
            val prop = PropertyDataExtended(prl, def)
            extproperties += prl to prop
            extpropRetrievers += { state, _, _, te ->
              val value = te.c.parts.getValue(prl.base).storage.get(prl.property)
              state.withProperty(prop, value)
            }
          } else {
            val prop = PropertyData(prl, def)
            properties += prl to prop
            propRetrievers += { state, _, _, te ->
              val value = te.c.parts.getValue(prl.base).storage.get(prl.property)
              @Suppress("UNCHECKED_CAST")
              state.withProperty(prop, prop.wrap(value) as PropertyData.Wrapper<Any?>)
            }
          }
        }
      }

      return Tuple5(
        ExtendedBlockState(block, properties.values.toTypedArray(), extproperties.values.toTypedArray()),
        properties,
        extproperties,
        propRetrievers,
        extpropRetrievers
      )
    }
  }

}