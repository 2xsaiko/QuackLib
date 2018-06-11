package core

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.core.modinterface.block
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.component.prefab.ComponentPlaceBlock
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.block.BlockReferenceByRL
import therealfarfetchd.quacklib.block.BlockReferenceDirect
import therealfarfetchd.quacklib.core.init.BlockConfigurationScopeImpl
import therealfarfetchd.quacklib.item.ItemReferenceByRL
import therealfarfetchd.quacklib.item.ItemReferenceDirect
import therealfarfetchd.quacklib.tools.ModContext

object APIImpl : QuackLibAPI {

  override val modContext = ModContext

  override var qlVersion: String = "unset"

  override fun getItem(name: String): ItemReference =
    ItemReferenceByRL(getResourceFromName(name))

  override fun getItem(item: Item): ItemReference =
    ItemReferenceDirect(item)

  override fun getItem(rl: ResourceLocation): ItemReference =
    ItemReferenceByRL(rl)

  override fun getBlock(name: String): BlockReference =
    BlockReferenceByRL(getResourceFromName(name))

  override fun getBlock(block: Block): BlockReference =
    BlockReferenceDirect(block)

  override fun getBlock(rl: ResourceLocation): BlockReference =
    BlockReferenceByRL(rl)

  override fun addItemToBlock(configurationScope: BlockConfiguration, name: String, op: ItemConfigurationScope.() -> Unit) {
    configurationScope as BlockConfigurationScopeImpl

    configurationScope.init.addItem(name) {
      apply(ComponentPlaceBlock(block(configurationScope.rl)))
      op(this)
    }
    // TODO add stuff to block
  }

  private fun getResourceFromName(name: String): ResourceLocation {
    val domain: String
    val iname: String

    if (name.contains(":")) {
      val (d, n) = name.split(":")
      domain = d
      iname = n
    } else {
      domain = ModContext.currentMod()?.modId ?: "minecraft"
      iname = name
    }

    return ResourceLocation(domain, iname)
  }

}