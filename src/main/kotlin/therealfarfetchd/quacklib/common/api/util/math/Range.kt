package therealfarfetchd.quacklib.common.api.util.math

infix fun ClosedFloatingPointRange<Double>.step(step: Double): Iterable<Double> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "$step must be greater than 0!" }
  val sequence = generateSequence(start) { previous ->
    if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}

infix fun ClosedFloatingPointRange<Float>.step(step: Float): Iterable<Float> {
  require(start.isFinite())
  require(endInclusive.isFinite())
  require(step > 0.0) { "$step must be greater than 0!" }
  val sequence = generateSequence(start) { previous ->
    if (previous == Float.POSITIVE_INFINITY) return@generateSequence null
    val next = previous + step
    if (next > endInclusive) null else next
  }
  return sequence.asIterable()
}