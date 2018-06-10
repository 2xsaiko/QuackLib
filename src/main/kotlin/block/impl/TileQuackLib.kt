package therealfarfetchd.quacklib.block.impl

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.quacklib.api.block.component.BlockComponentCapability
import therealfarfetchd.quacklib.api.block.component.BlockComponentTickable
import therealfarfetchd.quacklib.api.block.component.BlockData
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration

open class TileQuackLib(def: BlockConfiguration) : TileEntity() {

  val components = def.components

  val capProviders = getComponentsOfType<BlockComponentCapability>()

  override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean =
    capProviders.any { it.hasCapability(getBlockData(), capability, facing) } ||
    super.hasCapability(capability, facing)

  override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? =
    capProviders.firstOrNull { it.hasCapability(getBlockData(), capability, facing) }?.getCapability(getBlockData(), capability, facing)
    ?: super.getCapability(capability, facing)

  class Tickable(def: BlockConfiguration) : TileQuackLib(def), ITickable {

    val needsTick = getComponentsOfType<BlockComponentTickable>()

    override fun update() {
      needsTick.forEach { it.onTick(getBlockData()) }
    }

  }

  protected fun getBlockData() = BlockData(world, pos, world.getBlockState(pos))

  protected inline fun <reified T : Any> getComponentsOfType(): List<T> =
    components.mapNotNull { it as? T }

}