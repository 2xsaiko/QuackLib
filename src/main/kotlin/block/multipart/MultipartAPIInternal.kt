package therealfarfetchd.quacklib.block.multipart

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.block.multipart.MultipartAPI
import therealfarfetchd.quacklib.api.item.component.ItemComponent

interface MultipartAPIInternal : MultipartAPI {

  fun createPlacementComponent(def: BlockConfiguration): ItemComponent

  fun registerBlock(e: RegistryEvent.Register<Block>, def: BlockConfiguration)

  fun onDrawOverlay(world: World, hit: RayTraceResult, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>)

}