package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentMultipart
import therealfarfetchd.quacklib.api.block.component.import
import therealfarfetchd.quacklib.api.block.multipart.PartSlot
import therealfarfetchd.quacklib.api.objects.block.Block

class SidedMultipart : BlockComponentMultipart,
                       BlockComponentDataImport {

  val facing = import<EnumFacing>()

  override fun getSlot(block: Block): PartSlot {
    return PartSlot.getFace(block[facing])
  }

}