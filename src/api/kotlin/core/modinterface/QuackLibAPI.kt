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
import therealfarfetchd.quacklib.api.render.model.ModelAPI
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope
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

  val modelAPI: ModelAPI

  val qlVersion: String

  fun getItem(name: String): ItemType

  fun getItem(item: MCItemType): ItemType

  fun getItem(rl: ResourceLocation): ItemType

  fun getBlock(name: String): BlockType

  fun getBlock(block: MCBlockType): BlockType

  fun getBlock(rl: ResourceLocation): BlockType

  fun convertItem(item: MCItem): Item

  fun getResourceFromContext(name: String): ResourceLocation

  fun <T> createBlockDataDelegate(part: BlockDataPart, name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T>

  fun <T, C : BlockComponentDataImport> createImportedValue(target: C): ImportedValue<T>

  fun <T, C : BlockComponentDataExport> createExportedValue(target: C, op: (C, Block) -> T): ExportedValue<C, T>

  fun <T, C : BlockComponentRenderProperties> addRenderProperty(target: C, ptype: KClass<*>, name: String, op: (RenderPropertyConfigurationScope<T>) -> Unit): RenderProperty<C, T>

  fun <T : Any> registerCapability(type: KClass<T>)

  fun notifySend(title: String, body: String?, expireTime: Long, icon: ResourceLocation?)

  fun <R> unsafeOps(op: (UnsafeScope) -> R): R

  fun logException(e: Throwable)

  fun openResource(rl: ResourceLocation, respectResourcePack: Boolean): InputStream?

  companion object {
    lateinit var impl: QuackLibAPI
  }

}