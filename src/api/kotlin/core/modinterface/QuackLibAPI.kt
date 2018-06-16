package therealfarfetchd.quacklib.api.core.modinterface

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.DataPartSerializationRegistry
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.tools.ModContext
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass

/**
 * This is the "API", but this doesn't mean you should use it.
 * There's probably better places to go for the stuff you need.
 */
interface QuackLibAPI {

  val modContext: ModContext

  val serializationRegistry: DataPartSerializationRegistry

  val qlVersion: String

  fun getItem(name: String): ItemReference

  fun getItem(item: Item): ItemReference

  fun getItem(rl: ResourceLocation): ItemReference

  fun getBlock(name: String): BlockReference

  fun getBlock(block: Block): BlockReference

  fun getBlock(rl: ResourceLocation): BlockReference

  fun addItemToBlock(configurationScope: BlockConfiguration, name: String, op: ItemConfigurationScope.() -> Unit)

  fun <T> createBlockDataDelegate(part: BlockDataPart, name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, render: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T>

  companion object {
    lateinit var impl: QuackLibAPI
  }

}