package therealfarfetchd.quacklib.api.core.extensions

import java.awt.Color
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier

operator fun <T, R> Function<T, R>.invoke(param1: T): R = apply(param1)
operator fun <T> Predicate<T>.invoke(param1: T): Boolean = test(param1)
operator fun <R> Supplier<R>.invoke(): R = get()

val Color.redf get() = red / 255f
val Color.greenf get() = green / 255f
val Color.bluef get() = blue / 255f
val Color.alphaf get() = alpha / 255f