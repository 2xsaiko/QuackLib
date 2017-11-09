@file:Suppress("MemberVisibilityCanPrivate", "unused")

package therealfarfetchd.quacklib.common.api.util

import java.io.Serializable

// kotlin.Pair

operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(first + other.first, second + other.second)

// kotlin.Triple

data class Tuple4<out A, out B, out C, out D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth)"
}

data class Tuple5<out A, out B, out C, out D, out E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth)"
}

data class Tuple6<out A, out B, out C, out D, out E, out F>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth)"
}

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh)"
}

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth)"
}

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H,
  val ninth: I
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth)"
}

data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H,
  val ninth: I,
  val tenth: J
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth)"
}

data class Tuple11<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J, out K>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H,
  val ninth: I,
  val tenth: J,
  val eleventh: K
) : Serializable {
  override fun toString() = "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth, $eleventh)"
}