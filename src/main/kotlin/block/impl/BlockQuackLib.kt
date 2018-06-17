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
import therealfarfetchd.math.Vec3
import therealfarfetchd.math.getDistance
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.block.data.*
import therealfarfetchd.quacklib.block.data.render.PropertyData
import therealfarfetchd.quacklib.block.data.render.PropertyDataExtended
import java.util.*
import kotlin.reflect.jvm.jvmName

@Suppress("OverridingDeprecatedMember")
class BlockQuackLib(val def: BlockConfiguration) : Block(def.material.also { tempBlockConf = def }), BlockExtraDebug {

  val needsTool = def.needsTool
  val tools = def.validTools

  val components = def.components.asReversed()

  val cActivate = getComponentsOfType<BlockComponentActivation>()
  val cDrops = getComponentsOfType<BlockComponentDrops>()
  val cPickBlock = getComponentsOfType<BlockComponentPickBlock>()
  val cPart = getComponentsOfType<BlockComponentData<*>>()
  val cCollision = getComponentsOfType<BlockComponentCollision>()
  val cMouseOver = getComponentsOfType<BlockComponentMouseOver>()
  val cCustomMouseOver = getComponentsOfType<BlockComponentCustomMouseOver>()

  val needsTile = getComponentsOfType<BlockComponentNeedTE>().isNotEmpty()
  val needsTick = getComponentsOfType<BlockComponentTickable>().isNotEmpty()

  lateinit var propRetrievers: Set<(IBlockState, IBlockAccess, BlockPos, TileQuackLib) -> IBlockState>
  lateinit var extpropRetrievers: Set<(IExtendedBlockState, IBlockAccess, BlockPos, TileQuackLib) -> IExtendedBlockState>

  lateinit var properties: Map<PropertyResourceLocation, PropertyData<*>>
  lateinit var extproperties: Map<PropertyResourceLocation, PropertyDataExtended<*>>

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()
    def.hardness?.also {
      setHardness(it)
    } ?: setBlockUnbreakable()

    cPart.forEach { it.part = PartAccessTokenImpl(it.rl) }
  }

  override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return cActivate
      .map { it.onActivated(BlockDataImpl(world, pos, state), playerIn, hand, facing, Vec3(hitX, hitY, hitZ)) }
      .any { it }
  }

  override fun createBlockState(): BlockStateContainer {
    val cPart = tempBlockConf.components.asReversed().mapNotNull { it as? BlockComponentData<*> }

    properties = emptyMap()
    extproperties = emptyMap()
    propRetrievers = emptySet()
    extpropRetrievers = emptySet()

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
            val value = te.parts.getValue(prl.base).storage.get(prl.property)
            state.withProperty(prop, value)
          }
        } else {
          val prop = PropertyData(prl, def)
          properties += prl to prop
          propRetrievers += { state, _, _, te ->
            val value = te.parts.getValue(prl.base).storage.get(prl.property)
            @Suppress("UNCHECKED_CAST")
            state.withProperty(prop, prop.wrap(value) as PropertyData.Wrapper<Any?>)
          }
        }
      }
    }

    return ExtendedBlockState(this, properties.values.toTypedArray(), extproperties.values.toTypedArray())
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

    cCollision.takeIf { it.isNotEmpty() }?.flatMap { it.getCollisionBoundingBoxes(data) }
    ?: setOf(FULL_BLOCK_AABB)
      .map { it.offset(pos) }
      .filter(entityBox::intersects)
      .also { collidingBoxes.addAll(it) }
  }

  // outline
  override fun getSelectedBoundingBox(state: IBlockState, world: World, pos: BlockPos): AxisAlignedBB {
    // TODO
    return super.getSelectedBoundingBox(state, world, pos)
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

  // TODO
  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
    // super.getDrops(drops, world, pos, state, fortune)
    // FIXME don't just cast this to World
    cDrops.forEach { drops += it.getDrops(BlockDataROImpl(world, pos, state)) }
  }

  override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
    return cPickBlock.firstOrNull()?.getPickBlock(BlockDataImpl(world, pos, state))
           ?: super.getPickBlock(state, target, world, pos, player)
  }

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

  companion object {
    private lateinit var tempBlockConf: BlockConfiguration

    val NOPE_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
  }

}