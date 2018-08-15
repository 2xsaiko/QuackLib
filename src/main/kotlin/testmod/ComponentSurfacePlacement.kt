package therealfarfetchd.quacklib.testmod

import net.minecraft.block.state.BlockFaceShape
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.data.data
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.orEmpty
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid
import therealfarfetchd.quacklib.api.tools.offset

class ComponentSurfacePlacement : BlockComponentPlacement<ComponentSurfacePlacement.Data>,
                                  BlockComponentData<ComponentSurfacePlacement.Data>,
                                  BlockComponentDataExport,
                                  BlockComponentRenderProperties,
                                  BlockComponentNeighborListener,
                                  BlockComponentPlacementCheck {

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "sidedplacement")

  val facing = export(Data::facing)

  val rpFacing = renderProperty<Facing>("facing") { output { block -> block.part.facing } } fix this

  override lateinit var part: PartAccessToken<Data>

  override fun initialize(block: Block, placer: EntityLivingBase, hand: EnumHand, facing: Facing, hit: Vec3) {
    block.part.facing = facing.opposite
  }

  override fun onNeighborChanged(block: Block, side: Facing) {
    block.worldMutable?.also { world ->
      val facing = block.part.facing
      if (side != facing) return

      if (!isValid(block.world, block.pos, side)) {
        world.breakBlock(block.pos)
      }
    }
  }

  override fun canPlaceBlockAt(world: World, pos: PositionGrid, side: Facing?): Boolean {
    if (side == null) return true
    return isValid(world, pos, side.opposite)
  }

  fun isValid(world: World, pos: PositionGrid, side: Facing): Boolean {
    val block = world.getBlock(pos.offset(side)).orEmpty()
    return block.getFaceShape(side.opposite) == BlockFaceShape.SOLID
  }

  override fun createPart(): Data = Data()

  class Data : BlockDataPart(version = 0) {

    var facing: Facing by data("facing", Facing.DOWN, sync = true)

  }

}