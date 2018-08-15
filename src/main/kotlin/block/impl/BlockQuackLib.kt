package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.particle.ParticleDigging
import net.minecraft.client.particle.ParticleManager
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.property.ExtendedBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import scala.Tuple5
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.core.extensions.toVec3
import therealfarfetchd.quacklib.api.core.extensions.toVec3i
import therealfarfetchd.quacklib.api.core.unsafe
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlock
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.hasComponent
import therealfarfetchd.quacklib.api.objects.item.MCItem
import therealfarfetchd.quacklib.api.objects.item.MCItemType
import therealfarfetchd.quacklib.api.objects.world.MCWorld
import therealfarfetchd.quacklib.api.objects.world.MCWorldMutable
import therealfarfetchd.quacklib.api.render.property.PropertyType
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.block.component.ComponentRenderProps
import therealfarfetchd.quacklib.block.data.PartAccessTokenImpl
import therealfarfetchd.quacklib.block.data.PropertyResourceLocation
import therealfarfetchd.quacklib.block.data.render.PropertyData
import therealfarfetchd.quacklib.block.data.render.PropertyDataExtended
import therealfarfetchd.quacklib.hax.ExtraData
import therealfarfetchd.quacklib.objects.block.BlockImpl
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.world.toWorld
import therealfarfetchd.quacklib.render.model.needsTESR
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.jvm.jvmName

private typealias SetPropertyRetrievers = MutableSet<(MCBlock, Block) -> MCBlock>
private typealias SetExtendedPropertyRetrievers = MutableSet<(IExtendedBlockState, Block) -> IExtendedBlockState>
private typealias MapProperties = MutableMap<PropertyResourceLocation, PropertyData<*>>
private typealias MapExtendedProperties = MutableMap<PropertyResourceLocation, PropertyDataExtended<*>>

@Suppress("OverridingDeprecatedMember")
class BlockQuackLib(val type: BlockType) : MCBlockType(type.material.also { tempBlockConf = type as BlockTypeImpl }), BlockExtraDebug {

  private var initDone = false

  val needsTool = type.needsTool
  val tools = type.validTools

  val components = type.components.asReversed()

  val cData = getComponentsOfType<BlockComponentData<*>>()

  val needsTile = getComponentsOfType<BlockComponentNeedTE>().isNotEmpty() || type.model.needsTESR()
  val needsTick = getComponentsOfType<BlockComponentTickable>().isNotEmpty()

  lateinit var propRetrievers: SetPropertyRetrievers
  lateinit var extpropRetrievers: SetExtendedPropertyRetrievers

  lateinit var properties: MapProperties
  lateinit var extproperties: MapExtendedProperties

  init {
    (type as? BlockTypeImpl)?.block = this
    registryName = type.registryName
    translationKey = type.registryName.toString()
    type.hardness?.also {
      setHardness(it)
    } ?: setBlockUnbreakable()

    initDone = true
    soundType = type.soundType
    cData.forEach { it.part = PartAccessTokenImpl(it.rl) }
  }

  fun getBlockImpl(world: MCWorld, pos: BlockPos): Block =
    BlockImpl.createExistingFromWorld(world.toWorld(), pos.toVec3i())

  override fun onBlockActivated(world: MCWorldMutable, pos: BlockPos, state: MCBlock, playerIn: EntityPlayer, hand: EnumHand, facing: Facing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return getBlockImpl(world, pos).onActivated(playerIn, hand, facing, Vec3(hitX, hitY, hitZ))
  }

  override fun createBlockState(): BlockStateContainer {
    val bs = createBlockState(this)

    properties = bs._2()
    extproperties = bs._3()
    propRetrievers = bs._4()
    extpropRetrievers = bs._5()

    return bs._1()
  }

  override fun getActualState(state: MCBlock, world: MCWorld, pos: BlockPos): MCBlock {
    val block = world.toWorld().getBlock(pos.toVec3i()) ?: return state
    return propRetrievers.fold(state) { acc, op -> op(acc, block) }
  }

  override fun getExtendedState(state: MCBlock, world: MCWorld, pos: BlockPos): MCBlock {
    val block = world.toWorld().getBlock(pos.toVec3i()) ?: return state
    state as? IExtendedBlockState ?: return state
    return extpropRetrievers.fold(state) { acc, op -> op(acc, block) }
  }

  // ray trace
  override fun collisionRayTrace(state: MCBlock, world: MCWorldMutable, pos: BlockPos, start: Vec3d, end: Vec3d): RayTraceResult? {
    return getBlockImpl(world, pos).raytrace(start.toVec3(), end.toVec3())
  }

  // ray trace
  override fun getBoundingBox(state: MCBlock, world: MCWorld, pos: BlockPos): AxisAlignedBB {
    return getBlockImpl(world, pos).getRaytraceBoundingBox() ?: NOPE_AABB
  }

  // collision
  override fun getCollisionBoundingBox(state: MCBlock, world: MCWorld, pos: BlockPos): AxisAlignedBB? {
    return getBlockImpl(world, pos).getCollisionBoundingBox()
  }

  // collision
  override fun addCollisionBoxToList(state: MCBlock, world: MCWorldMutable, pos: BlockPos, entityBox: AxisAlignedBB, collidingBoxes: MutableList<AxisAlignedBB>, entityIn: Entity?, isActualState: Boolean) {
    collidingBoxes.addAll(getBlockImpl(world, pos).getCollisionBoundingBoxes().map { it.offset(pos) }.filter { it.intersects(entityBox) })
  }

  // outline
  override fun getSelectedBoundingBox(state: MCBlock, world: MCWorldMutable, pos: BlockPos): AxisAlignedBB {
    // TODO
    return super.getSelectedBoundingBox(state, world, pos)
  }

  override fun neighborChanged(state: MCBlock, world: MCWorldMutable, pos: BlockPos, block: MCBlockType, fromPos: BlockPos) {
    val facing = fromPos.subtract(pos).let { Facing.getFacingFromVector(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()) }
    getBlockImpl(world, pos).onNeighborChanged(facing)
  }

  override fun canPlaceBlockOnSide(world: MCWorldMutable, pos: BlockPos, side: Facing): Boolean {
    return super.canPlaceBlockAt(world, pos) && type.behavior.canPlaceBlockAt(world.toWorld(), pos.toVec3i(), side)
  }

  override fun canPlaceBlockAt(world: MCWorldMutable, pos: BlockPos): Boolean {
    return super.canPlaceBlockAt(world, pos) && type.behavior.canPlaceBlockAt(world.toWorld(), pos.toVec3i(), null)
  }

  override fun getStrongPower(state: MCBlock, world: MCWorld, pos: BlockPos, side: Facing): Int {
    return getBlockImpl(world, pos).getStrongPower(side.opposite)
  }

  override fun getWeakPower(state: MCBlock, world: MCWorld, pos: BlockPos, side: Facing): Int {
    return getBlockImpl(world, pos).getWeakPower(side.opposite)
  }

  override fun canProvidePower(state: MCBlock): Boolean {
    return type.hasComponent<BlockComponentRedstone>()
  }

  override fun canConnectRedstone(state: MCBlock, world: MCWorld, pos: BlockPos, side: Facing?): Boolean {
    if (side == null) return false

    return getBlockImpl(world, pos).canConnectRedstone(side.opposite)
  }

  override fun getRenderLayer(): BlockRenderLayer {
    return BlockRenderLayer.CUTOUT
  }

  @Suppress("UNCHECKED_CAST")
  override fun addInformation(world: MCWorldMutable, pos: BlockPos, state: MCBlock, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {
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

  private fun getComponentInfo(world: MCWorldMutable, pos: BlockPos, state: MCBlock, c: BlockComponent): List<String> {
    val bi = getBlockImpl(world, pos)

    var descString = " - "
    descString += c::class.simpleName ?: c::class.qualifiedName ?: c::class.jvmName
    if (c is BlockComponentRegistered) descString += " (${c.rl})"
    if (c is BlockComponentInfo) descString += ":"

    var list = listOf(descString)
    if (c is BlockComponentInfo) {
      list += c.getInfo(bi).map { it.prependIndent("     ") }
    }
    return list
  }

  override fun getHarvestLevel(state: MCBlock): Int {
    if (tools.isEmpty()) return -1
    return tools.first().level
  }

  override fun isToolEffective(type: String?, state: MCBlock): Boolean {
    if (!needsTool) return true
    return type in tools.map { it.toolName }
  }

  override fun hasTileEntity(state: MCBlock): Boolean = needsTile

  override fun createTileEntity(world: MCWorldMutable?, state: MCBlock): TileQuackLib? =
    when {
      needsTick -> TileQuackLib.Tickable(type)
      needsTile -> TileQuackLib(type)
      else -> null
    }

  override fun getMetaFromState(state: MCBlock?): Int = 0

  // rendering
  // cullface on other blocks
  override fun isOpaqueCube(state: MCBlock): Boolean {
    // TODO query model
    return if (initDone) type.behavior.isNormalBlock()
    else tempBlockConf.components.none { it is BlockComponentCollision }
  }

  // no rendering
  override fun isNormalCube(state: MCBlock): Boolean {
    return type.behavior.isNormalBlock()
  }

  // rendering (?)
  // block creates a shadow
  // block suffocates player
  override fun isFullCube(state: MCBlock): Boolean {
    return true
    //    return type.behavior.isNormalBlock()
  }

  override fun getAmbientOcclusionLightValue(state: IBlockState?): Float {
    return super.getAmbientOcclusionLightValue(state)
  }

  override fun causesSuffocation(state: MCBlock): Boolean {
    return super.causesSuffocation(state)
  }

  // TODO
  override fun getItemDropped(state: MCBlock?, rand: Random?, fortune: Int): MCItemType? = null

  override fun getDrops(drops: NonNullList<MCItem>, world: MCWorld, pos: BlockPos, state: MCBlock, fortune: Int) {
    // super.getDrops(drops, world, pos, state, fortune)
    unsafe {
      drops.addAll(getBlockImpl(world, pos).getDrops(fortune).map { it.toMCItem() })
    }
  }

  override fun getPickBlock(state: MCBlock, target: RayTraceResult, world: MCWorldMutable, pos: BlockPos, player: EntityPlayer): MCItem {
    return unsafe {
      getBlockImpl(world, pos).getPickBlock(target, player).toMCItem()
    }
  }

  override fun addDestroyEffects(world: MCWorldMutable, pos: BlockPos, manager: ParticleManager): Boolean {
    val state = world.getBlockState(pos)
    val bb = state.getBoundingBox(world, pos)
    val particlesX = maxOf(1, (4 * (bb.maxX - bb.minX)).roundToInt())
    val particlesY = maxOf(1, (4 * (bb.maxY - bb.minY)).roundToInt())
    val particlesZ = maxOf(1, (4 * (bb.maxZ - bb.minZ)).roundToInt())

    for (i in 0 until particlesX) {
      for (j in 0 until particlesY) {
        for (k in 0 until particlesZ) {
          val xOff = (i + 0.5) / 4.0
          val yOff = (j + 0.5) / 4.0
          val zOff = (k + 0.5) / 4.0
          val x = bb.minX + pos.x + (xOff * (bb.maxX - bb.minX))
          val y = bb.minY + pos.y + (yOff * (bb.maxY - bb.minY))
          val z = bb.minZ + pos.z + (zOff * (bb.maxZ - bb.minZ))
          val xVel = bb.minX + (xOff - 0.5) * (bb.maxX - bb.minX) / 2
          val yVel = bb.minY + (yOff - 0.5) * (bb.maxY - bb.minY) / 2
          val zVel = bb.minZ + (zOff - 0.5) * (bb.maxZ - bb.minZ) / 2
          manager.addEffect(object : ParticleDigging(world, x, y, z, xVel, yVel, zVel, state) {}.setBlockPos(pos))
        }
      }
    }

    return true
  }

  override fun toString(): String {
    return "Block '${type.registryName}' (${components.size} components)"
  }

  inline fun <reified T : BlockComponent> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

  companion object {

    private lateinit var tempBlockConf: BlockTypeImpl

    val NOPE_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

    // FIXME use custom tuple here, not scalas
    fun createBlockState(block: MCBlockType?): Tuple5<BlockStateContainer, MapProperties, MapExtendedProperties, SetPropertyRetrievers, SetExtendedPropertyRetrievers> {
      val cRender = tempBlockConf.components.asReversed().mapNotNull { it as? BlockComponentRenderProperties }

      val properties: MapProperties = mutableMapOf()
      val extproperties: MapExtendedProperties = mutableMapOf()
      val propRetrievers: SetPropertyRetrievers = mutableSetOf()
      val extpropRetrievers: SetExtendedPropertyRetrievers = mutableSetOf()

      //      for (c in cPart) {
      //        val defs = c.createPart().defs
      //        for ((name, def) in defs) {
      //          if (!def.render) continue
      //
      //          val prl = PropertyResourceLocation(c.rl, name)
      //          val needsExt = def.validValues == null || def.validValues!!.size > 32
      //          if (needsExt) {
      //            val prop = PropertyDataExtended(prl, def)
      //            extproperties += prl to prop
      //            extpropRetrievers += { state, _, _, te ->
      //              val value = te.c.parts.getValue(prl.base).storage.get(prl.property)
      //              state.withProperty(prop, value)
      //            }
      //          } else {
      //            val prop = PropertyData(prl, def)
      //            properties += prl to prop
      //            propRetrievers += { state, _, _, te ->
      //              val value = te.c.parts.getValue(prl.base).storage.get(prl.property)
      //              @Suppress("UNCHECKED_CAST")
      //              state.withProperty(prop, prop.wrap(value) as PropertyData.Wrapper<Any?>)
      //            }
      //          }
      //        }
      //      }

      unsafe {
        for (c in cRender) {
          val data = ExtraData.getOrNull(c, ComponentRenderProps.Key) ?: continue
          data.props.forEach {
            val pt = it.getMCProperty()

            @Suppress("UNCHECKED_CAST")
            when (pt) {
              is PropertyType.Standard -> {
                val prop = pt.prop as PropertyData<Any?>
                properties += PropertyResourceLocation(c.rl, it.name) to prop
                propRetrievers += { state: MCBlock, block: Block ->
                  state.withProperty(prop, prop.wrap(it.getValue(block))!!)
                }
              }
              is PropertyType.Extended -> {
                val prop = pt.prop as PropertyDataExtended<Any?>
                extproperties += PropertyResourceLocation(c.rl, it.name) to prop
                extpropRetrievers += { state: IExtendedBlockState, block: Block ->
                  state.withProperty(prop, it.getValue(block))
                }
              }
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