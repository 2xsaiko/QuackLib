package therealfarfetchd.quacklib.common.extensions

import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Created by marco on 13.07.17.
 */

private val Boolean.bitmask: Byte
  get() = if (this) -1 else 0

private val Boolean.bitmaskS: Short
  get() = if (this) -1 else 0

private val Boolean.bitmaskI: Int
  get() = if (this) -1 else 0

fun flags(b0: Boolean = false, b1: Boolean = false, b2: Boolean = false, b3: Boolean = false,
          b4: Boolean = false, b5: Boolean = false, b6: Boolean = false, b7: Boolean = false): Byte =
    (b0.bitmask and 1) or
        (b1.bitmask and 2) or
        (b2.bitmask and 4) or
        (b3.bitmask and 8) or
        (b4.bitmask and 16) or
        (b5.bitmask and 32) or
        (b6.bitmask and 64) or
        (b7.bitmask and -128)

fun flags(b: Byte): BooleanArray {
  val arr = BooleanArray(8)
  for (i in 0..7) {
    arr[i] = (b and (1 shl i).toByte()) != 0.toByte()
  }
  return arr
}

fun flags(b0: Boolean = false, b1: Boolean = false, b2: Boolean = false, b3: Boolean = false,
          b4: Boolean = false, b5: Boolean = false, b6: Boolean = false, b7: Boolean = false,
          b8: Boolean = false, b9: Boolean = false, b10: Boolean = false, b11: Boolean = false,
          b12: Boolean = false, b13: Boolean = false, b14: Boolean = false, b15: Boolean = false): Short =
    (b0.bitmaskS and 1) or
        (b1.bitmaskS and 2) or
        (b2.bitmaskS and 4) or
        (b3.bitmaskS and 8) or
        (b4.bitmaskS and 16) or
        (b5.bitmaskS and 32) or
        (b6.bitmaskS and 64) or
        (b7.bitmaskS and 128) or
        (b8.bitmaskS and 256) or
        (b9.bitmaskS and 512) or
        (b10.bitmaskS and 1024) or
        (b11.bitmaskS and 2048) or
        (b12.bitmaskS and 4096) or
        (b13.bitmaskS and 8192) or
        (b14.bitmaskS and 16384) or
        (b15.bitmaskS and -32768)

fun flags(b: Short): BooleanArray {
  val arr = BooleanArray(16)
  for (i in 0..15) {
    arr[i] = (b and (1 shl i).toShort()) != 0.toShort()
  }
  return arr
}

fun flags(b0: Boolean = false, b1: Boolean = false, b2: Boolean = false, b3: Boolean = false,
          b4: Boolean = false, b5: Boolean = false, b6: Boolean = false, b7: Boolean = false,
          b8: Boolean = false, b9: Boolean = false, b10: Boolean = false, b11: Boolean = false,
          b12: Boolean = false, b13: Boolean = false, b14: Boolean = false, b15: Boolean = false,
          b16: Boolean = false, b17: Boolean = false, b18: Boolean = false, b19: Boolean = false,
          b20: Boolean = false, b21: Boolean = false, b22: Boolean = false, b23: Boolean = false,
          b24: Boolean = false, b25: Boolean = false, b26: Boolean = false, b27: Boolean = false,
          b28: Boolean = false, b29: Boolean = false, b30: Boolean = false, b31: Boolean = false): Int =
    (b0.bitmaskI and 1) or
        (b1.bitmaskI and 2) or
        (b2.bitmaskI and 4) or
        (b3.bitmaskI and 8) or
        (b4.bitmaskI and 16) or
        (b5.bitmaskI and 32) or
        (b6.bitmaskI and 64) or
        (b7.bitmaskI and 128) or
        (b8.bitmaskI and 256) or
        (b9.bitmaskI and 512) or
        (b10.bitmaskI and 1024) or
        (b11.bitmaskI and 2048) or
        (b12.bitmaskI and 4096) or
        (b13.bitmaskI and 8192) or
        (b14.bitmaskI and 16384) or
        (b15.bitmaskI and 32768) or
        (b16.bitmaskI and 65536) or
        (b17.bitmaskI and 131072) or
        (b18.bitmaskI and 262144) or
        (b19.bitmaskI and 524288) or
        (b20.bitmaskI and 1048576) or
        (b21.bitmaskI and 2097152) or
        (b22.bitmaskI and 4194304) or
        (b23.bitmaskI and 8388608) or
        (b24.bitmaskI and 16777216) or
        (b25.bitmaskI and 33554432) or
        (b26.bitmaskI and 67108864) or
        (b27.bitmaskI and 134217728) or
        (b28.bitmaskI and 268435456) or
        (b29.bitmaskI and 536870912) or
        (b30.bitmaskI and 1073741824) or
        (b31.bitmaskI and -2147483648)

fun flags(b: Int): BooleanArray {
  val arr = BooleanArray(32)
  for (i in 0..31) {
    arr[i] = (b and (1 shl i)) != 0
  }
  return arr
}