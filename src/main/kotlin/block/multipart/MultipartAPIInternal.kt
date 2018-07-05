package therealfarfetchd.quacklib.block.multipart

import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.event.RegistryEvent
import therealfarfetchd.quacklib.api.block.multipart.MultipartAPI
import therealfarfetchd.quacklib.api.item.component.ItemComponent
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl

interface MultipartAPIInternal : MultipartAPI {

  fun createPlacementComponent(type: BlockType): ItemComponent

  fun registerBlock(e: RegistryEvent.Register<Block>, type: BlockTypeImpl)

  fun onDrawOverlay(world: World, hit: RayTraceResult, player: EntityPlayer, left: MutableList<String>, right: MutableList<String>)

}