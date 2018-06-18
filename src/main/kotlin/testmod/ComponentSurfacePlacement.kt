package therealfarfetchd.quacklib.testmod

import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.BlockComponentData
import therealfarfetchd.quacklib.api.block.component.BlockComponentDataExport
import therealfarfetchd.quacklib.api.block.component.ExportedData
import therealfarfetchd.quacklib.api.block.component.export
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.data.data
import therealfarfetchd.quacklib.testmod.ComponentSurfacePlacement.Exported

class ComponentSurfacePlacement : BlockComponentData<ComponentSurfacePlacement.Data>,
                                  BlockComponentDataExport<ComponentSurfacePlacement, Exported> {

  override val exported = Exported(this)

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "sidedplacement")

  override lateinit var part: PartAccessToken<Data>

  override fun createPart(): Data = Data()

  class Data : BlockDataPart(version = 0) {

    var facing: EnumFacing by data("facing", EnumFacing.DOWN, render = true)

  }

  class Exported(target: ComponentSurfacePlacement) : ExportedData<Exported, ComponentSurfacePlacement>(target) {

    val facing = export(Data::facing)

  }

}