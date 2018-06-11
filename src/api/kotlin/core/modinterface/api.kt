@file:Suppress("NOTHING_TO_INLINE")

package therealfarfetchd.quacklib.api.core.modinterface

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.block.BlockReference
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.item.ItemReference
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope

inline fun item(item: Item): ItemReference = QuackLibAPI.impl.getItem(item)

inline fun item(name: String): ItemReference = QuackLibAPI.impl.getItem(name)

inline fun item(rl: ResourceLocation): ItemReference = QuackLibAPI.impl.getItem(rl)

inline fun block(block: Block): BlockReference = QuackLibAPI.impl.getBlock(block)

inline fun block(name: String): BlockReference = QuackLibAPI.impl.getBlock(name)

inline fun block(rl: ResourceLocation): BlockReference = QuackLibAPI.impl.getBlock(rl)

inline fun BlockConfigurationScope.withPlacementItem(name: String = this.name, noinline op: ItemConfigurationScope.() -> Unit = {}) =
  QuackLibAPI.impl.addItemToBlock(this, name, op)