package therealfarfetchd.quacklib.block.impl

import net.minecraft.block.Block
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.api.block.component.BlockComponentActivation
import therealfarfetchd.quacklib.api.tools.Logger
import therealfarfetchd.quacklib.core.init.BlockConfigurationScopeImpl

class BlockQuackLib(def: BlockConfigurationScopeImpl) : Block(def.material) {

  val needsTool = def.needsTool
  val tools = def.validTools

  val components = def.components

  val cActivate = getComponentsOfType<BlockComponentActivation>()

  init {
    registryName = def.rl
    unlocalizedName = def.rl.toString()
    def.hardness?.also {
      setHardness(it)
    } ?: setBlockUnbreakable()

    if (tools.size > 1) Logger.warn("More than 1 harvest tool is currently not supported. Ignoring ${tools.size - 1} tools")
  }

  override fun onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    return cActivate
      .map { it.onActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ) }
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

  private inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}