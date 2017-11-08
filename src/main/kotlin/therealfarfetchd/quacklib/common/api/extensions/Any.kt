package therealfarfetchd.quacklib.common.api.extensions

fun <I : O, O> I.mapIf(condition: Boolean, op: (I) -> O): O = if (condition) op(this) else this

fun <I : O, O> I.mapUnless(condition: Boolean, op: (I) -> O): O = if (!condition) op(this) else this