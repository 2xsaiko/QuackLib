package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import java.util.*

class BlockQuackLib(val def: BlockConfiguration) : Block(def.material) {

  val needsTool = def.needsTool
  val tools = def.validTools

  val components = def.components.asReversed()

  val cActivate = getComponentsOfType<BlockComponentActivation>()
  val cDrops = getComponentsOfType<BlockComponentDrops>()
  val cPickBlock = getComponentsOfType<BlockComponentPickBlock>()

  val needsTile = getComponentsOfType<BlockComponentNeedTE>().isNotEmpty()
  val needsTick = getComponentsOfType<BlockComponentTickable>().isNotEmpty()

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()
    def.hardness?.also {
      setHardness(it)
    } ?: setBlockUnbreakable()
  }

  override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return cActivate
      .map { it.onActivated(BlockData(worldIn, pos, state), playerIn, hand, facing, Vec3(hitX, hitY, hitZ)) }
      .any { it }
  }

  override fun createBlockState(): BlockStateContainer {
    return super.createBlockState()
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

  // TODO
  override fun getItemDropped(state: IBlockState?, rand: Random?, fortune: Int): Item? = null

  override fun getDrops(drops: NonNullList<ItemStack>, world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int) {
    // super.getDrops(drops, world, pos, state, fortune)
    // FIXME don't just cast this to World
    cDrops.forEach { drops += it.getDrops(BlockData(world as World, pos, state)) }
  }

  override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
    return cPickBlock.firstOrNull()?.getPickBlock(BlockData(world, pos, state))
           ?: super.getPickBlock(state, target, world, pos, player)
  }

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}