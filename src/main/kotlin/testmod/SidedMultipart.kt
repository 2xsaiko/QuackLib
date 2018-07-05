package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataImport
import therealfarfetchd.quacklib.api.block.component.BlockComponentMultipart
import therealfarfetchd.quacklib.api.block.component.ImportedData
import therealfarfetchd.quacklib.api.block.multipart.PartSlot
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.testmod.SidedMultipart.Imported

class SidedMultipart : BlockComponentMultipart,
                       BlockComponentDataImport<SidedMultipart, Imported> {

  override val imported = Imported(this)

  override fun getSlot(block: Block): PartSlot {
    return PartSlot.getFace(block[imported.facing])
  }

  class Imported(target: SidedMultipart) : ImportedData<Imported, SidedMultipart>(target) {

    val facing = import<EnumFacing>()

  }

}