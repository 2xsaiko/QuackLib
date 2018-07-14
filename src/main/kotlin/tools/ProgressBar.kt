package therealfarfetchd.quacklib.tools

import net.minecraftforge.fml.common.ProgressManager
import therealfarfetchd.quacklib.api.tools.isDebugMode
import kotlin.reflect.KClass

fun <R> progressbar(name: String, steps: Int, time: Boolean = isDebugMode, op: ProgressBarScope.() -> R): R {
  val bar = ProgressManager.push(name, steps, time)
  return try {
    op(ProgressBarScope(bar))
  } finally {
    ProgressManager.pop(bar)
  }
}

class ProgressBarScope(private val bar: ProgressManager.ProgressBar) {

  fun step(message: String) {
    bar.step(message)
  }

  fun step(cls: KClass<*>, vararg extra: String) {
    bar.step(cls.java, *extra)
  }

}