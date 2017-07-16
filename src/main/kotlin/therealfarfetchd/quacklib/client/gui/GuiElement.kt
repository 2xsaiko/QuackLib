package therealfarfetchd.quacklib.client.gui

import net.minecraft.client.renderer.GlStateManager
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

/**
 * Created by marco on 16.07.17.
 */
abstract class GuiElement : IGuiElement {
  var x: Int by number()
  var y: Int by number()
  override final var width: Int by number()
  override final var height: Int by number()
  var relx: RelativeX by transform<RelativeX, String>({ name.toLowerCase() }, { RelativeX.byName(this) })
  var rely: RelativeY by transform<RelativeY, String>({ name.toLowerCase() }, { RelativeY.byName(this) })

  internal lateinit var parent: IGuiElement
  override final var elements: Set<GuiElement> = emptySet()

  internal var properties: Map<String, Any?> = emptyMap()

  init {
    x = 0
    y = 0
    width = 20
    height = 20
    relx = RelativeX.Left
    rely = RelativeY.Top
  }

  open fun transformAndRender(mouseX: Int, mouseY: Int) {
    GlStateManager.pushMatrix()
    GlStateManager.translate(getEffectiveX(x), getEffectiveY(y), 1f)
    GlStateManager.pushMatrix()
    render(mouseX, mouseY)
    GlStateManager.popMatrix()
    elements.forEach { it.transformAndRender(mouseX, mouseY) }
    GlStateManager.popMatrix()
  }

  open fun getEffectiveX(x: Int): Float {
    when (relx) {
      RelativeX.Left -> return x * 1f
      RelativeX.Center -> return x + parent.width / 2f - width / 2f
      RelativeX.Right -> return x + parent.width - width * 1f
    }
  }

  open fun getEffectiveY(y: Int): Float {
    when (rely) {
      RelativeY.Top -> return y * 1f
      RelativeY.Center -> return y + parent.height / 2f - height / 2f
      RelativeY.Bottom -> return y + parent.height - height * 1f
    }
  }

  private class mapper<T> : ReadWriteProperty<GuiElement, T> {
    override fun getValue(thisRef: GuiElement, property: KProperty<*>): T {
      @Suppress("UNCHECKED_CAST")
      return thisRef.properties[property.name] as T
    }

    override fun setValue(thisRef: GuiElement, property: KProperty<*>, value: T) {
      thisRef.properties += property.name to value
    }
  }

  private class transform<T, Store>(private val serialize: (T).() -> Store, private val deserialize: (Store).() -> T) : ReadWriteProperty<GuiElement, T> {
    override fun getValue(thisRef: GuiElement, property: KProperty<*>): T {
      @Suppress("UNCHECKED_CAST")
      return deserialize(thisRef.properties[property.name] as Store)
    }

    override fun setValue(thisRef: GuiElement, property: KProperty<*>, value: T) {
      thisRef.properties += property.name to serialize(value)
    }
  }

  private class number<T> : ReadWriteProperty<GuiElement, T> {
    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    override fun getValue(thisRef: GuiElement, property: KProperty<*>): T {
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

    override fun setValue(thisRef: GuiElement, property: KProperty<*>, value: T) {
      thisRef.properties += property.name to BigDecimal(value.toString())
    }
  }

}