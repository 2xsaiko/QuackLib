package therealfarfetchd.quacklib.block.multipart.mcmp

import mcmultipart.api.multipart.IMultipartTile
import therealfarfetchd.quacklib.block.impl.TileQuackLib

class TileMultipartQuackLib(val te: TileQuackLib) : IMultipartTile {

  override fun getTileEntity(): TileQuackLib = te

}