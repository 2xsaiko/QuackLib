package therealfarfetchd.quacklib.common.api.extensions

import java.lang.invoke.MethodHandle

@Suppress("NOTHING_TO_INLINE")
inline fun <R, T> MethodHandle.invokeKt(t: T): R = MethodHandleIsStupidAndRefusesToWorkInKotlin.call(this, t)