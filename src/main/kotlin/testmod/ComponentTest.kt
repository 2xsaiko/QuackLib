package therealfarfetchd.quacklib.testmod

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Random
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentActivation
import therealfarfetchd.quacklib.api.block.component.BlockComponentData
import therealfarfetchd.quacklib.api.block.component.BlockComponentInfo
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.data.data
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.tools.Facing

class ComponentTest : BlockComponentActivation, BlockComponentData<ComponentTest.Data>, BlockComponentInfo {

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "testcomponent")

  override lateinit var part: PartAccessToken<Data>

  override fun onActivated(data: Block, player: EntityPlayer, hand: EnumHand, facing: Facing, hit: Vec3): Boolean {
    data.worldMutable?.also { world ->
      if (world.isClient) return@also
      data.part.counter = (data.part.counter + 1) % 11
      data.part.code = (0 until 5).map { 'A' + Random.nextInt(26) }.joinToString("")
      data.part.facing = facing
      data.part.item = player.getHeldItem(hand).copy()
      world.syncClient(data.pos)
    }
    return true
  }

  override fun getInfo(data: Block): List<String> =
    listOf("counter: ${data.part.counter}")

  override fun createPart() = Data()

  class Data : BlockDataPart(version = 0) {

    var counter by data("counter", default = 0, render = true, validValues = 0..10)

    var code by data("code", default = "AAAAA", render = true)

    var list by data("list", default = listOf("test", 100, mapOf("key" to true), true), render = true)

    var facing by data("facing", default = EnumFacing.UP, render = true)

    var item by data("item", default = ItemStack.EMPTY, render = true)

  }

}