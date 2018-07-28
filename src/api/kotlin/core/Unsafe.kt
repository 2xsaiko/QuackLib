package therealfarfetchd.quacklib.api.core

//import therealfarfetchd.quacklib.api.objects.entity.UnsafeExtEntity
import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import therealfarfetchd.quacklib.api.objects.block.UnsafeExtBlock
import therealfarfetchd.quacklib.api.objects.item.UnsafeExtItem
import therealfarfetchd.quacklib.api.objects.world.UnsafeExtWorld
import therealfarfetchd.quacklib.api.render.property.UnsafeExtRP

/**
 * Method to access vanilla classes that are hidden behind abstractions.
 */
interface Unsafe {

  val self: Unsafe get() = this

}

interface UnsafeScope : Unsafe,
                        UnsafeExtBlock,
                        UnsafeExtItem,
                        // UnsafeExtEntity,
                        UnsafeExtWorld,
                        UnsafeExtRP

@Suppress("NOTHING_TO_INLINE")
inline fun <R> unsafe(noinline op: UnsafeScope.() -> R): R =
  QuackLibAPI.impl.unsafeOps(op)