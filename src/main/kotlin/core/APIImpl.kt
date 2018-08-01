package therealfarfetchd.quacklib.core

//import therealfarfetchd.quacklib.block.multipart.cbmp.MultipartAPIImpl as CBMPAPI
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.component.*
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.DataPartSerializationRegistry
import therealfarfetchd.quacklib.api.core.UnsafeScope
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.block.orEmpty
import therealfarfetchd.quacklib.api.objects.item.*
import therealfarfetchd.quacklib.api.render.model.ModelAPI
import therealfarfetchd.quacklib.api.render.property.RenderProperty
import therealfarfetchd.quacklib.api.render.property.RenderPropertyConfigurationScope
import therealfarfetchd.quacklib.api.tools.Logger
import therealfarfetchd.quacklib.api.tools.isDebugMode
import therealfarfetchd.quacklib.block.component.ComponentRenderProps
import therealfarfetchd.quacklib.block.component.ExportedValueImpl
import therealfarfetchd.quacklib.block.component.ImportedValueImpl
import therealfarfetchd.quacklib.block.data.DataPartSerializationRegistryImpl
import therealfarfetchd.quacklib.block.data.ValuePropertiesImpl
import therealfarfetchd.quacklib.block.data.get
import therealfarfetchd.quacklib.block.data.set
import therealfarfetchd.quacklib.block.multipart.MultipartAPIInternal
import therealfarfetchd.quacklib.hax.ExtraData
import therealfarfetchd.quacklib.objects.block.BlockTypeImpl
import therealfarfetchd.quacklib.objects.block.DeferredBlockTypeImpl
import therealfarfetchd.quacklib.objects.item.DeferredItemTypeImpl
import therealfarfetchd.quacklib.objects.item.ItemImpl
import therealfarfetchd.quacklib.objects.item.ItemTypeImpl
import therealfarfetchd.quacklib.render.model.ModelAPIImpl
import therealfarfetchd.quacklib.render.property.RenderPropertyConfigurationScopeImpl
import therealfarfetchd.quacklib.render.property.RenderPropertyImpl
import therealfarfetchd.quacklib.tools.ModContext
import therealfarfetchd.quacklib.tools.getCallStack
import therealfarfetchd.quacklib.tools.getResourceFromName
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import therealfarfetchd.quacklib.block.multipart.mcmp.MultipartAPIImpl as MCMPAPI

object APIImpl : QuackLibAPI {

  override val modContext = ModContext

  override val serializationRegistry: DataPartSerializationRegistry = DataPartSerializationRegistryImpl

  override val multipartAPI: MultipartAPIInternal = MCMPAPI

  override val modelAPI: ModelAPI = ModelAPIImpl

  override var qlVersion: String = "unset"

  override fun getItem(name: String): ItemType =
    getItem(getResourceFromName(name))

  override fun getItem(item: MCItemType): ItemType =
    ItemTypeImpl.getItem(item)

  override fun getItem(rl: ResourceLocation): ItemType =
    if (DeferredItemTypeImpl.isInit) DeferredItemTypeImpl(rl) else ItemTypeImpl.getItem(rl).orEmpty()

  override fun getBlock(name: String): BlockType =
    getBlock(getResourceFromName(name))

  override fun getBlock(block: MCBlockType): BlockType =
    BlockTypeImpl.getBlock(block)

  override fun getBlock(rl: ResourceLocation): BlockType =
    if (DeferredBlockTypeImpl.isInit) DeferredBlockTypeImpl(rl) else BlockTypeImpl.getBlock(rl).orEmpty()

  override fun convertItem(item: MCItem): Item = ItemImpl(item)

  override fun getResourceFromContext(name: String): ResourceLocation =
    getResourceFromName(name)

  @Suppress("UNCHECKED_CAST")
  override fun <T> createBlockDataDelegate(part: BlockDataPart, name: String, type: KClass<*>, default: T, persistent: Boolean, sync: Boolean, validValues: List<T>?): ReadWriteProperty<BlockDataPart, T> {
    val delegate = object : ReadWriteProperty<BlockDataPart, T> {

      @Suppress("UNCHECKED_CAST")
      override fun getValue(thisRef: BlockDataPart, property: KProperty<*>): T {
        return part.storage.get(name) as T
      }

      override fun setValue(thisRef: BlockDataPart, property: KProperty<*>, value: T) {
        part.storage.set(name, value)
      }

    }

    if (name in part.defs) error("Duplicate name")

    part.addDefinition(name, ValuePropertiesImpl(name, type as KClass<Any>, default, persistent, sync, validValues))

    return delegate
  }

  override fun <T, C : BlockComponentDataImport> createImportedValue(target: C): ImportedValue<T> {
    return ImportedValueImpl()
  }

  override fun <T, C : BlockComponentDataExport> createExportedValue(target: C, op: (C, Block) -> T): ExportedValue<C, T> {
    return ExportedValueImpl { data -> op(target, data) }
  }

  override fun <T, C : BlockComponentRenderProperties> addRenderProperty(target: C, ptype: KClass<*>, name: String, op: (RenderPropertyConfigurationScope<T>) -> Unit): RenderProperty<C, T> {
    val hasAccess = getCallStack()
      .drop(1) // this
      .first().methodName == "<init>"

    if (!hasAccess) error("Render properties need to be defined in the component constructor!")

    val rcs = RenderPropertyConfigurationScopeImpl<T>(name).also(op)

    val rp = RenderPropertyImpl(target::class, target.rl, name, ptype, rcs.outputOp, { rcs.constraints.all { op -> op(it) } }, null)

    ExtraData.get(target, ComponentRenderProps.Key).props += rp

    return rp
  }

  override fun notifySend(title: String, body: String?, expireTime: Long, icon: ResourceLocation?) {
    QuackLib.proxy.addNotification(title, body, expireTime, icon)
  }

  override fun <T : Any> registerCapability(type: KClass<T>) {
    therealfarfetchd.quacklib.tools.registerCapability(type)
  }

  override fun openResource(rl: ResourceLocation, respectResourcePack: Boolean): InputStream? {
    return QuackLib.proxy.openResource(rl, respectResourcePack)
  }

  override fun <R> unsafeOps(op: (UnsafeScope) -> R): R {
    return op(UnsafeImpl)
  }

  override fun logException(e: Throwable) {
    Logger.error(e)
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    sw.toString().lines().forEach {
      if (isDebugMode) Logger.error(it)
      else Logger.debug(it)
    }
  }

}