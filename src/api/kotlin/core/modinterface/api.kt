@file:Suppress("NOTHING_TO_INLINE")

package therealfarfetchd.quacklib.api.core.modinterface

import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.api.objects.block.BlockType
import therealfarfetchd.quacklib.api.objects.block.MCBlockType
import therealfarfetchd.quacklib.api.objects.item.ItemType
import therealfarfetchd.quacklib.api.objects.item.MCItemType
import java.io.InputStream

inline fun <R> lockMod(noinline op: () -> R): R = QuackLibAPI.impl.modContext.lockMod(op)

inline fun item(item: MCItemType): ItemType = QuackLibAPI.impl.getItem(item)

inline fun item(name: String): ItemType = QuackLibAPI.impl.getItem(name)

inline fun item(rl: ResourceLocation): ItemType = QuackLibAPI.impl.getItem(rl)

inline fun block(block: MCBlockType): BlockType = QuackLibAPI.impl.getBlock(block)

inline fun block(name: String): BlockType = QuackLibAPI.impl.getBlock(name)

inline fun block(rl: ResourceLocation): BlockType = QuackLibAPI.impl.getBlock(rl)

inline fun logException(e: Throwable) =
  lockMod { QuackLibAPI.impl.logException(e) }

inline fun openResource(rl: ResourceLocation, respectResourcePack: Boolean = false): InputStream? =
  QuackLibAPI.impl.openResource(rl, respectResourcePack)

inline fun notifySend(title: String, body: String? = null, expireTime: Long = 5000, icon: ResourceLocation? = null) =
  QuackLibAPI.impl.notifySend(title, body, expireTime, icon)