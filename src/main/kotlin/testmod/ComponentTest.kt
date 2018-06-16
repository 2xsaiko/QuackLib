package therealfarfetchd.quacklib.testmod

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import therealfarfetchd.math.Random
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.component.BlockComponentActivation
import therealfarfetchd.quacklib.api.block.component.BlockComponentData
import therealfarfetchd.quacklib.api.block.component.BlockComponentInfo
import therealfarfetchd.quacklib.api.block.data.BlockData
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.data.data

class ComponentTest : BlockComponentActivation, BlockComponentData<ComponentTest.Data>, BlockComponentInfo {

  override val rl: ResourceLocation = ResourceLocation("qltestmod", "testcomponent")

  override lateinit var part: PartAccessToken<Data>

  override fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean {
    data.part.counter = (data.part.counter + 1) % 11
    data.world.markBlockRangeForRenderUpdate(data.pos, data.pos)
    data.part.code = (0 until 5).map { 'A' + Random.nextInt(26) }.joinToString("")
    return true
  }

  override fun getInfo(data: BlockData): List<String> =
    listOf("counter: ${data.part.counter}")

  override fun createPart() = Data()

  class Data : BlockDataPart(version = 0) {

    var counter by data("counter", default = 0, persistent = true, render = true, validValues = 0..10)

    var code by data("code", default = "AAAAA", persistent = true, render = true)

    var list by data("list", default = listOf("test", 100, mapOf("key" to true), true), persistent = true, render = true)

  }

}