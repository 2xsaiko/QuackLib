package therealfarfetchd.quacklib.api.core.modinterface

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.tools.ModContext

interface QuackLibAPI {

  val modContext: ModContext

  val qlVersion: String

  fun getItem(name: String): ItemReference

  fun getItem(item: Item): ItemReference

  fun getItem(rl: ResourceLocation): ItemReference

  fun getBlock(name: String): BlockReference

  fun getBlock(block: Block): BlockReference

  fun getBlock(rl: ResourceLocation): BlockReference

  companion object {
    lateinit var impl: QuackLibAPI
  }

}