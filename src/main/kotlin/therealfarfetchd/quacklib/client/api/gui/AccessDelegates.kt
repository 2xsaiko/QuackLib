@file:Suppress("ClassName")

package therealfarfetchd.quacklib.client.api.gui

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * Created by marco on 16.07.17.
 */

class mapper<T> : ReadWriteProperty<IGuiElement, T> {
  override fun getValue(thisRef: IGuiElement, property: KProperty<*>): T {
    @Suppress("UNCHECKED_CAST")
    return thisRef.properties[property.name] as T
  }

  override fun setValue(thisRef: IGuiElement, property: KProperty<*>, value: T) {
    thisRef.properties[property.name] = value
  }
}

class transform<T, Store>(private val serialize: (T).() -> Store, private val deserialize: (Store).() -> T) : ReadWriteProperty<IGuiElement, T> {
  override fun getValue(thisRef: IGuiElement, property: KProperty<*>): T {
    @Suppress("UNCHECKED_CAST")
    return deserialize(thisRef.properties[property.name] as Store)
  }

  override fun setValue(thisRef: IGuiElement, property: KProperty<*>, value: T) {
    thisRef.properties[property.name] = serialize(value)
  }
}

class number<T> : ReadWriteProperty<IGuiElement, T> {
  @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
  override fun getValue(thisRef: IGuiElement, property: KProperty<*>): T {
    val bigdec = thisRef.properties[property.name] as BigDecimal
    return when (property.returnType.jvmErasure) {
      Byte::class -> bigdec.toByte()
      Short::class -> bigdec.toShort()
      Char::class -> bigdec.toChar()
      Int::class -> bigdec.toInt()
      Long::class -> bigdec.toLong()
      Float::class -> bigdec.toFloat()
      Double::class -> bigdec.toDouble()
      BigInteger::class -> bigdec.toBigInteger()
      BigDecimal::class -> bigdec
      else -> throw IllegalStateException("Invalid number type ${property.returnType.jvmErasure}")
    } as T
  }

  override fun setValue(thisRef: IGuiElement, property: KProperty<*>, value: T) {
    thisRef.properties[property.name] = BigDecimal(value.toString())
  }
}
