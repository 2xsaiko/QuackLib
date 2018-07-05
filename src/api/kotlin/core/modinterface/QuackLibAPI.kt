package therealfarfetchd.quacklib.api.core.modinterface

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.DataPartSerializationRegistry
import therealfarfetchd.quacklib.api.block.multipart.MultipartAPI
import therealfarfetchd.quacklib.api.core.UnsafeScope
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItem
import therealfarfetchd.quacklib.api.objects.item.MCItemType
import therealfarfetchd.quacklib.api.tools.ModContext
import java.io.InputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass

/**
 * This is the "API", but this doesn't mean you should use it.
 * There's probably better places to go for the stuff you need.
 */
interface QuackLibAPI {

  val modContext: ModContext

  val serializationRegistry: DataPartSerializationRegistry

  val multipartAPI: MultipartAPI

  val qlVersion: String

  fun getItem(name: String): ItemType

  fun getItem(item: MCItemType): ItemType

  fun getItem(rl: ResourceLocation): ItemType

  fun getBlock(name: String): BlockType

  fun getBlock(block: MCBlockType): BlockType

  fun getBlock(rl: ResourceLocation): BlockType

  fun convertItem(item: MCItem): Item

  fun <T> createBlockDataDelegate(part: BlockDataPart, name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, render: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T>

  fun <T, C : BlockComponentDataImport<C, D>, D : ImportedData<D, C>> createImportedValue(target: C): ImportedValue<T>

  fun <R, C : BlockComponentDataExport<C, D>, D : ExportedData<D, C>> createExportedValue(target: C, op: (C, Block) -> R): ExportedValue<D, R>

  fun <T : Any> registerCapability(type: KClass<T>)

  fun <R> unsafeOps(op: (UnsafeScope) -> R): R

  fun logException(e: Throwable)

  fun openResource(rl: ResourceLocation, respectResourcePack: Boolean): InputStream?

  companion object {
    lateinit var impl: QuackLibAPI
  }

}