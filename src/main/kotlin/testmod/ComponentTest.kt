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
import therealfarfetchd.quacklib.api.block.data.*

class ComponentTest : BlockComponentActivation, BlockComponentData<ComponentTest.Data>, BlockComponentInfo {

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "testcomponent")

  override lateinit var part: PartAccessToken<Data>

  override fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean {
    if (!data.world.isRemote) {
      data.part.counter = (data.part.counter + 1) % 11
      data.part.code = (0 until 5).map { 'A' + Random.nextInt(26) }.joinToString("")
      data.part.facing = facing
      data.part.item = player.getHeldItem(hand).copy()
      data.world.notifyBlockUpdate(data.pos, data.state, data.state, 3)
    }
    return true
  }

  override fun getInfo(data: BlockDataRO): List<String> =
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