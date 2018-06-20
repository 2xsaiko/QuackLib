package therealfarfetchd.quacklib.testmod

import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.data.data
import therealfarfetchd.quacklib.testmod.ComponentSurfacePlacement.Exported

class ComponentSurfacePlacement : BlockComponentPlacement<ComponentSurfacePlacement.Data>,
                                  BlockComponentData<ComponentSurfacePlacement.Data>,
                                  BlockComponentDataExport<ComponentSurfacePlacement, Exported> {

  override val exported = Exported(this)

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "sidedplacement")

  override lateinit var part: PartAccessToken<Data>

  override fun initialize(world: IBlockAccess, pos: BlockPos, part: Data, placer: EntityLivingBase, hand: EnumHand, facing: EnumFacing, hit: Vec3) {
    part.facing = facing.opposite
  }

  override fun createPart(): Data = Data()

  class Data : BlockDataPart(version = 0) {

    var facing: EnumFacing by data("facing", EnumFacing.DOWN, render = true)

  }

  class Exported(target: ComponentSurfacePlacement) : ExportedData<Exported, ComponentSurfacePlacement>(target) {

    val facing = export(Data::facing)

  }

}