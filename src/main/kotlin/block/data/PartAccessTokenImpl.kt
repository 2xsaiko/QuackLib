package therealfarfetchd.quacklib.block.data

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.data.*
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class PartAccessTokenImpl<out T : BlockDataPart>(val rl: ResourceLocation) : PartAccessToken<T> {

  @Suppress("UNCHECKED_CAST")
  override fun retrieve(data: BlockData): T {
    val (world, pos, _) = data
    val te = world.getTileEntity(pos) as? TileQuackLib
             ?: error("Missing Tile Entity at $pos!")
    return te.parts.getValue(rl) as T
  }

}