package therealfarfetchd.quacklib.block.multipart

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration

object DummyAPI : MultipartAPIInternal {

  override fun createPlacementComponent(def: BlockConfiguration) = throwError()

  override fun registerBlock(e: RegistryEvent.Register<Block>, def: BlockConfiguration) = throwError()

  override fun onDrawOverlay(world: World, hit: RayTraceResult, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>) {}

  private fun throwError(): Nothing = error("Multipart API not loaded.")

}