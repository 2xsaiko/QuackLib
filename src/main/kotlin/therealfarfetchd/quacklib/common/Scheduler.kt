package therealfarfetchd.quacklib.common

/**
 * Created by marco on 08.07.17.
 */

object Scheduler {
  private var tasks: Set<Pair<Int, () -> Any?>> = emptySet()

  fun schedule(ticks: Int, op: () -> Any?) {
    tasks += ticks to op
  }

  internal fun tick() {
    tasks.filter { it.first <= 0 }.also { tasks -= it }.forEach { it.second() }
    tasks = tasks.map { it.first - 1 to it.second }.toSet()
  }
}