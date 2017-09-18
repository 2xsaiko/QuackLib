package therealfarfetchd.quacklib.common.api.extensions

infix fun <A, B, C> ((A) -> B).compose(op2: (B) -> C): (A) -> C {
  return { a -> op2(this(a)) }
}