package therealfarfetchd.quacklib.api.core.extensions

fun <T : R, R> T.letIf(condition: Boolean, op: (T) -> R): R =
  if (condition) op(this) else this

fun <T : R, R> T.runIf(condition: Boolean, op: T.() -> R): R =
  if (condition) op() else this