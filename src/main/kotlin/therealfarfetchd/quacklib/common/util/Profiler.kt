package therealfarfetchd.quacklib.common.util

import therealfarfetchd.quacklib.QuackLib

object Profiler {

  operator fun <R> invoke(name: String, maxtime: Long = 500, op: () -> R): R {
    val startTime = System.currentTimeMillis()
    val ret = op()
    val elapsedTime = System.currentTimeMillis() - startTime
    val flag = elapsedTime > maxtime
    if (QuackLib.debug || flag) {
      val s = "$name took longer than expected! (${elapsedTime}ms > ${maxtime}ms)"
      val s1 = "$name took ${elapsedTime}ms."
      QuackLib.Logger.warn(if (flag) s else s1)
    }
    return ret
  }

}