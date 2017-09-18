package therealfarfetchd.quacklib.common.api.util

/**
 * Created by marco on 28.05.17.
 */
interface IView<in K, V> {
  operator fun get(k: K): V
  operator fun set(k: K, v: V)
}