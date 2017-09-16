package therealfarfetchd.quacklib.common.qblock

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import therealfarfetchd.quacklib.common.util.shutupForge
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.superclasses

@Suppress("UNCHECKED_CAST")
object WrapperImplManager {
  private var modifiers: Set<KClass<*>> = emptySet()
  private var templates: Map<Set<KClass<*>>, WrapperTemplate> = emptyMap()

  private var blocksMap: Map<KClass<QBlock>, Block> = emptyMap()
  private var itemsMap: Map<KClass<QBlock>, Item> = emptyMap()

  init {
    registerWrapper {}
  }

  fun registerModifier(kClass: KClass<*>) {
    modifiers += kClass
  }

  fun getModifiers(kClass: KClass<*>): Set<KClass<*>> {
    return if (kClass in modifiers) setOf(kClass)
    else kClass.superclasses.flatMap(this::getModifiers).toSet()
  }

  fun registerWrapper(vararg kClass: KClass<*>, op: WrapperTemplate.() -> Unit) {
    val t = WrapperTemplate().also(op)
    templates += kClass.toSet() to t
  }

  fun getTemplate(vararg modifiers: KClass<*>): WrapperTemplate = templates[modifiers.toSet()] ?: error("There's no template registered for this combination of modifiers!")

  fun <T : QBlock> getTemplate(kClass: KClass<T>): WrapperTemplate = getTemplate(*getModifiers(kClass).toTypedArray())

  fun <T : QBlock> getContainer(kClass: KClass<T>): Block {
    val kClass1 = kClass as KClass<QBlock>
    return if (kClass in blocksMap) blocksMap[kClass]!!
    else {
      val block = getTemplate(kClass).containerOp { createInstance(kClass) }
      val qb = createInstance(kClass)
      shutupForge {
        block.registryName = qb.blockType
      }
      block.unlocalizedName = qb.blockType.toString()
      blocksMap += kClass1 to block
      block
    }
  }

  fun <T : QBlock> container(kClass: KClass<T>): Lazy<Block> = lazy { getContainer(kClass) }

  fun <T : QBlock> getItem(kClass: KClass<T>): Item {
    val kClass1 = kClass as KClass<QBlock>
    return if (kClass in itemsMap) itemsMap[kClass]!!
    else {
      val item = getTemplate(kClass).itemOp({ createInstance(kClass) }, getContainer(kClass))
      val qb = createInstance(kClass)
      shutupForge {
        item.registryName = qb.blockType
      }
      item.unlocalizedName = qb.blockType.toString()
      itemsMap += kClass1 to item
      item
    }
  }

  fun <T : QBlock> item(kClass: KClass<T>): Lazy<Item> = lazy { getItem(kClass) }

  fun <T : QBlock> createTileEntity(kClass: KClass<T>): (QBlock) -> TileEntity {
    return getTemplate(kClass).teOp
  }

  fun <T : QBlock> createInstance(kClass: KClass<T>): T {
    return getTemplate(kClass).qbOp(kClass as KClass<QBlock>) as T
  }

  class WrapperTemplate {
    var containerOp: (() -> QBlock) -> Block = { QBContainer(it) }
    var itemOp: (() -> QBlock, Block) -> Item = { _, b -> ItemBlock(b) }
    var teOp: (QBlock) -> TileEntity = ::QBContainerTile
    var qbOp: (KClass<QBlock>) -> QBlock = { it.createInstance() }

    fun container(op: (() -> QBlock) -> Block) {
      containerOp = op
    }

    fun item(op: (() -> QBlock, Block) -> Item) {
      itemOp = op
    }

    fun te(op: (QBlock) -> TileEntity) {
      teOp = op
    }

    fun qb(op: (KClass<QBlock>) -> QBlock) {
      qbOp = op
    }

    fun inherit(vararg modifiers: KClass<*>) {
      val t = getTemplate(*modifiers)
      containerOp = t.containerOp
      itemOp = t.itemOp
      teOp = t.teOp
      qbOp = t.qbOp
    }
  }
}