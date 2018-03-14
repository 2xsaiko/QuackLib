package therealfarfetchd.quacklib.common.api.qblock

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T> QBContainerTile.etype() = object : ReadOnlyProperty<QBContainerTile, T> {
  override fun getValue(thisRef: QBContainerTile, property: KProperty<*>): T {
    @Suppress("UNCHECKED_CAST")
    return thisRef.qb as T
  }
}