package therealfarfetchd.quacklib.api.core

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.core.modinterface.lockMod

/**
 * Method to access vanilla classes that are hidden behind abstractions.
 */
interface Unsafe

@Suppress("NOTHING_TO_INLINE")
inline fun <R> unsafe(noinline op: Unsafe.() -> R): R =
  lockMod { QuackLibAPI.impl.unsafeOps(op) }