package therealfarfetchd.quacklib.common.util

import kotlin.reflect.KProperty0

/**
 * Created by marco on 13.07.17.
 */
class ChangeListener(vararg val values: KProperty0<*>) {

  private var map: Map<KProperty0<*>, Any?> = mkMap()

  private fun mkMap(): Map<KProperty0<*>, Any?> = values.associate { it to it.get() }

  fun valuesChanged(update: Boolean = true): Boolean {
    val new = mkMap()
    val result = map == new
    if (update) map = new
    return !result
  }

}