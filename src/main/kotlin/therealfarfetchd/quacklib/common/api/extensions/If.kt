package therealfarfetchd.quacklib.common.api.extensions

// Sometimes these kind of if's are better.

interface ConditionalPart<out T> {
  val value: T
  val isPresent: Boolean
}

object FalsePart : ConditionalPart<Nothing> {
  override val value: Nothing
    get() = error("Value not present!")
  override val isPresent: Boolean = false
}

data class TruePart<out T>(override val value: T) : ConditionalPart<T> {
  override val isPresent: Boolean = true
}

inline fun <T> Boolean.iif(op: () -> T): ConditionalPart<T> = if (this) TruePart(op()) else FalsePart

inline fun <T> Boolean.iunless(op: () -> T): ConditionalPart<T> = if (!this) TruePart(op()) else FalsePart

inline fun <R, I : R> ConditionalPart<I>.ielse(op: () -> R): R = if (isPresent) value else op()

fun <R, A : R, B : R> Boolean.select(trueV: A, falseV: B) = if (this) trueV else falseV