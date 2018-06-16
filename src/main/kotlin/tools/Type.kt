package therealfarfetchd.quacklib.tools

fun Class<*>.getType(): String {
  if (isArray) return "[${componentType.getType()}"

  return when (this) {
    java.lang.Boolean.TYPE -> "Z"
    java.lang.Byte.TYPE -> "B"
    java.lang.Short.TYPE -> "S"
    java.lang.Character.TYPE -> "C"
    java.lang.Integer.TYPE -> "I"
    java.lang.Long.TYPE -> "J"
    java.lang.Float.TYPE -> "F"
    java.lang.Double.TYPE -> "D"
    java.lang.Void.TYPE -> "V"
    else -> "L${name.replace('.', '/')};"
  }
}

fun loadClass(type: String): Class<*>? {
  val arraydepth = type.takeWhile { it == '[' }.length
  if (arraydepth > 0) return Class.forName(type.replace('/', '.'))
  val typespec = type.drop(arraydepth)
  val objtype = typespec.firstOrNull() ?: return null

  return when (objtype) {
    'Z' -> java.lang.Boolean.TYPE
    'B' -> java.lang.Byte.TYPE
    'S' -> java.lang.Short.TYPE
    'C' -> java.lang.Character.TYPE
    'I' -> java.lang.Integer.TYPE
    'J' -> java.lang.Long.TYPE
    'F' -> java.lang.Float.TYPE
    'D' -> java.lang.Double.TYPE
    'V' -> java.lang.Void.TYPE
    'L' -> {
      val tn = typespec.drop(1).takeWhile { it != ';' }.replace('/', '.')
      return Class.forName(tn)
    }
    else -> null
  }
}