package therealfarfetchd.quacklib.common.extensions

fun <T> T.mapIf(condition: Boolean, op: (T) -> T): T = if (condition) op(this) else this

fun <T> T.mapUnless(condition: Boolean, op: (T) -> T): T = mapIf(!condition, op)