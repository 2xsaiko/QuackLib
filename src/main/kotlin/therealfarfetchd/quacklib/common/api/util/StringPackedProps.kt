package therealfarfetchd.quacklib.common.api.util

import therealfarfetchd.quacklib.common.api.extensions.unsigned

class StringPackedProps(var string: String = "") {
  var position = 0
  var lastReadStart = 0

  fun getBoolean() = getInt(1) == 1

  fun getByte(max: Byte) = getInt(max.unsigned).toByte()

  fun getShort(max: Short) = getInt(max.unsigned).toShort()

  fun getInt(max: Int): Int {
    lastReadStart = position
    val c = _getChar()
    if (max !in 0 until chars.length) error("max length out of range")
    val r = chars.indexOf(c)
    if (r == -1 || r > max) error("result out of range")
    return r
  }

  fun getChar() = getString(1).padStart(1, padChar)[0]

  private fun _getChar(): Char {
    if (position >= string.length) error("EOF")
    return string[position++]
  }

  fun getString(len: Short): String {
    lastReadStart = position
    val sb = StringBuilder()
    for (i in 0 until len.unsigned) sb.append(_getChar())
    return sb.toString().trimStart(padChar)
  }

  fun putBoolean(b: Boolean) = putInt(if (b) 1 else 0)

  fun putByte(b: Byte) = putInt(b.unsigned)

  fun putShort(s: Short) = putInt(s.unsigned)

  fun putInt(i: Int) {
    if (i !in 0 until chars.length) error("argument out of range")
    putChar(chars[i])
  }

  fun putChar(c: Char) {
    string += c
  }

  fun putString(s: String, len: Short) {
    if (s.length > len.unsigned) error("string is longer than max length")
    string += s.padStart(len.unsigned, padChar)
  }

  val hasNext: Boolean
    get() = string.length > position

  companion object {
    val chars = " !\"#\$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~"
    val padChar = '…' // padding used at the beginning of strings that are not the same length as their maxlength
  }
}