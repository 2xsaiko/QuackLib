package therealfarfetchd.quacklib.common.api.extensions

import java.lang.invoke.MethodHandle

fun <R, T> MethodHandle.invokeKt(t: T): R = MethodHandleIsStupidAndRefusesToWorkInKotlin.call(this, t)