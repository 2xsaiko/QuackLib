package therealfarfetchd.quacklib.common.api.util

import mcmultipart.api.multipart.IMultipart
import mcmultipart.multipart.MultipartRegistry
import net.minecraft.block.Block
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.ProgressManager
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.discovery.asm.ModAnnotation
import net.minecraftforge.fml.relauncher.ReflectionHelper
import net.minecraftforge.fml.relauncher.Side
import org.apache.logging.log4j.core.Logger
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.api.autoconf.FeatureManager
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultipart
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*

/**
 * Created by marco on 30.06.17.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class BlockDef(
  val registerModels: Boolean = true,
  val creativeTab: String = ModID,
  val dependencies: String = "",
  val layout: BlockClassLayout = BlockClassLayout.Standard,
  val metaModels: IntArray = intArrayOf(0)
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ItemDef(
  val registerModels: Boolean = true,
  val creativeTab: String = ModID,
  val metaModels: IntArray = intArrayOf(1)
)

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class AutoLoad

interface ICommonItemDef {
  val item: Item?
  val registerModels: Boolean
  val metaModels: Collection<Int>
}

interface IBlockDefinition : ICommonItemDef {
  val block: Block
  val multipart: Boolean

  companion object {
    var definitions: Set<IBlockDefinition> = emptySet(); private set

    @Suppress("UNCHECKED_CAST")
    fun populateBlockDefs(asmData: ASMDataTable) {
      val data = asmData.getAll(BlockDef::class.java.name)
      val bar = ProgressManager.push("Preparing block registry", data.size)
      for (d in data) {
        var warnings: List<String> = emptyList()

        val mainClass = shutupForge { findClass(d.className)!! }
        bar.step(mainClass.java)
        QuackLib.Logger.info("Found block ${mainClass.simpleName}")

        val layout = (d.annotationInfo["layout"] as? ModAnnotation.EnumHolder)?.let { BlockClassLayout.valueOf(it.value) } ?: BlockClassLayout.Standard
        val metaModels = (d.annotationInfo["metaModels"] as? IntArray ?: intArrayOf(0)).toSet()

        val requiredFeatures = (d.annotationInfo["dependencies"] as? String ?: "").split(";").filter(String::isNotBlank)
        if (requiredFeatures.isNotEmpty()) {
          var status = "all of which are activated."
          var skip = false
          var missingMods: List<String> = emptyList()

          FeatureManager.push()
          missingMods += requiredFeatures.filterNot { FeatureManager.dependSoft(it) }
          FeatureManager.reset(missingMods.isEmpty())

          if (missingMods.isNotEmpty()) {
            status = "but the following could not be activated: ${missingMods.joinToString()}. Not loading block."
            skip = true
          }
          QuackLib.Logger.info("Block requires these features: ${requiredFeatures.joinToString()}, $status")
          if (skip) continue
        }

        when (layout) {
          BlockClassLayout.Standard -> {
            val companionClass = mainClass.companionObject ?: throw IllegalBlockDefLayoutException("QBlock class $mainClass should have companion object!")
            val companion = mainClass.companionObjectInstance!!

            val getblock = findProperty(companionClass, "Block") ?: throw IllegalBlockDefLayoutException("QBlock class $mainClass should have a `Block` field in it's companion object holding the `Block` instance!")
            val getitem = findProperty(companionClass, "Item")

            val blockInstance = shutupForge { getblock.get(companion).takeIf { it is Block } as Block? }
                                ?: throw IllegalBlockDefLayoutException("QBlock class $mainClass should have a `Block` field in it's companion object holding the `Block` instance!")

            val item = getitem?.get(companion)?.takeIf {
              Item::class.isSuperclassOf(it::class) ||
              { warnings += "Item object found, but it doesn't extend Item! Ignoring."; false }()
            } as Item?

            blockInstance.setCreativeTab(getCreativeTab(d.annotationInfo["creativeTab"] as? String))

            val multipart = IQBlockMultipart::class in mainClass.allSuperclasses

            if (multipart) {
              if (blockInstance is IMultipart) {
                MultipartRegistry.INSTANCE.registerPartWrapper(blockInstance, blockInstance as IMultipart)
              } else {
                warnings += "QBlock is a multipart, but it's corresponding block isn't!"
              }
            }

            QuackLib.Logger.info(" -> Item?: ${if (item != null) "Yes" else "No"}")
            QuackLib.Logger.info(" -> Multipart?: ${if (multipart) "Yes" else "No"}")
            for (warning in warnings) {
              QuackLib.Logger.warn(" -> WARNING: $warning")
            }

            definitions += object : IBlockDefinition {
              override val metaModels: Collection<Int> = metaModels
              override val item: Item? = item
              override val block: Block = blockInstance
              override val multipart: Boolean = multipart
              override val registerModels: Boolean = d.annotationInfo["registerModels"] as? Boolean ?: true
            }
          }
          BlockClassLayout.StaticBlock -> {
            val block = (mainClass.objectInstance ?: throw IllegalBlockDefLayoutException("Block ${d.className} should be an object!")) as? Block
                        ?: throw IllegalBlockDefLayoutException("Block ${d.className} doesn't extend net.minecraft.block.Block!")

            val getitem = findProperty(mainClass, "Item")
            val item = getitem?.get(block)?.takeIf {
              Item::class.isSuperclassOf(it::class) ||
              { warnings += "Item object found, but it doesn't extend Item! Ignoring."; false }()
            } as Item?

            block.setCreativeTabFromName(d.annotationInfo["creativeTab"] as? String)
            block.unlocalizedName = block.registryName.toString()

            item?.also { it.registryName = block.registryName }

            QuackLib.Logger.info(" -> Item?: ${if (item != null) "Yes" else "No"}")
            QuackLib.Logger.info(" -> Multipart?: not supported yet")
            for (warning in warnings) {
              QuackLib.Logger.warn(" -> WARNING: $warning")
            }

            definitions += object : IBlockDefinition {
              override val metaModels: Collection<Int> = metaModels
              override val item: Item? = item
              override val block: Block = block
              override val multipart: Boolean = false
              override val registerModels: Boolean = d.annotationInfo["registerModels"] as? Boolean ?: true
            }
          }
        }
      }
      ProgressManager.pop(bar)
    }
  }
}

interface IItemDefinition : ICommonItemDef {
  override val item: Item

  companion object {
    var definitions: Set<IItemDefinition> = emptySet(); private set

    fun populateItemDefs(asmData: ASMDataTable) {
      val data = asmData.getAll(ItemDef::class.java.name)
      val bar = ProgressManager.push("Preparing item registry", data.size)
      for (d in data) {
        var warnings: List<String> = emptyList()

        val mainClass = shutupForge { findClass(d.className)!! }
        bar.step(mainClass.java)
        val item = (mainClass.objectInstance ?: throw IllegalBlockDefLayoutException("Item ${d.className} should be an object!")) as? Item
                   ?: throw IllegalBlockDefLayoutException("Item ${d.className} doesn't extend net.minecraft.item.Item!")

        item.setCreativeTabFromName(d.annotationInfo["creativeTab"] as? String)
        item.unlocalizedName = item.registryName.toString()

        QuackLib.Logger.info("Found item ${mainClass.simpleName}")
        for (warning in warnings) {
          QuackLib.Logger.warn(" -> WARNING: $warning")
        }

        definitions += object : IItemDefinition {
          override val item: Item = item
          override val registerModels: Boolean = d.annotationInfo["registerModels"] as? Boolean ?: true
          override val metaModels: Collection<Int> = (d.annotationInfo["metaModels"] as? IntArray ?: intArrayOf(0)).toSet()
        }
      }
      ProgressManager.pop(bar)
    }
  }
}

class IllegalBlockDefLayoutException(message: String) : RuntimeException(message)

private fun findClass(className: String): KClass<*>? = try {
  Class.forName(className).kotlin
} catch (e: ClassNotFoundException) {
  null
}

enum class BlockClassLayout {
  Standard, // Class extends QBlock, companion object with Block and optional Item properties
  StaticBlock, // Object extends Block with optional Item property
}

/**
 * Make forge not spew "dangerous alternative prefix" messages in this block.
 */
fun <T> shutupForge(op: () -> T): T {
  val log = FMLLog.log
  val privateConfig = ReflectionHelper.findField(Logger::class.java, "privateConfig")[log]
  val intLevelF = ReflectionHelper.findField(privateConfig.javaClass, "intLevel")
  val intLevel = intLevelF[privateConfig] as Int
  intLevelF[privateConfig] = 299 // disable WARN logging

  try {
    return op()
  } finally {
    intLevelF[privateConfig] = intLevel
  }
}

private fun getCreativeTab(name: String?) = when (FMLCommonHandler.instance().side) {
  Side.CLIENT -> CreativeTabs.CREATIVE_TAB_ARRAY.firstOrNull { it.tabLabel == name ?: ModID }
  Side.SERVER -> null
}


private fun Block.setCreativeTabFromName(name: String?) = getCreativeTab(name)?.also { setCreativeTab(it) }

private fun Item.setCreativeTabFromName(name: String?) = getCreativeTab(name)?.also { creativeTab = it }

@Suppress("UNCHECKED_CAST")
private fun findProperty(clazz: KClass<*>, fieldName: String): KProperty1<Any, Any?>? = clazz.memberProperties.find { it.name == fieldName } as KProperty1<Any, Any?>?