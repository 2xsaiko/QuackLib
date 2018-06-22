package therealfarfetchd.quacklib.block.multipart.cbmp

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.block.multipart.MultipartAPIInternal

object MultipartAPIImpl : MultipartAPIInternal {

  override fun createPlacementComponent(def: BlockConfiguration): ItemComponent {
    return ComponentPlaceMultipart(def)
  }

  override fun registerBlock(e: RegistryEvent.Register<Block>, def: BlockConfiguration) {
    PartRegistrationHelper.registerBlock(def)
  }

  override fun onDrawOverlay(world: World, hit: RayTraceResult, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {

  }

}