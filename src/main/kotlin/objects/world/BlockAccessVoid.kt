package therealfarfetchd.quacklib.objects.world

import net.minecraft.block.state.IBlockState
import net.minecraft.init.Biomes
import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.WorldType
import net.minecraft.world.biome.Biome

object BlockAccessVoid : IBlockAccess {

  override fun isSideSolid(pos: BlockPos?, side: EnumFacing?, _default: Boolean): Boolean {
    return false
  }

  override fun isAirBlock(pos: BlockPos?): Boolean {
    return true
  }

  override fun getStrongPower(pos: BlockPos?, direction: EnumFacing?): Int {
    return 0
  }

  override fun getCombinedLight(pos: BlockPos?, lightValue: Int): Int {
    return 0
  }

  override fun getTileEntity(pos: BlockPos?): TileEntity? {
    return null
  }

  override fun getBlockState(pos: BlockPos?): IBlockState {
    return Blocks.AIR.defaultState
  }

  override fun getBiome(pos: BlockPos?): Biome {
    return Biomes.DEFAULT
  }

  override fun getWorldType(): WorldType {
    return WorldType.DEFAULT
  }

}