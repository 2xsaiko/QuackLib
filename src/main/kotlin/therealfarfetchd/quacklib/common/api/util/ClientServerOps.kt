package therealfarfetchd.quacklib.common.api.util

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
import therealfarfetchd.quacklib.common.api.extensions.ielse
import therealfarfetchd.quacklib.common.api.extensions.iif
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
class ClientServerSeparateData<T>(op: (client: Boolean) -> T) : ReadWriteProperty<Any?, T> {
  var cdata: T = op(true)
  var sdata: T = op(false)

  override fun getValue(thisRef: Any?, property: KProperty<*>): T {
    return when (FMLCommonHandler.instance().effectiveSide) {
      Side.CLIENT -> cdata
      Side.SERVER -> sdata
    }
  }

  override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    when (FMLCommonHandler.instance().effectiveSide) {
      Side.CLIENT -> cdata = value
      Side.SERVER -> sdata = value
    }
  }
}

fun <T : Any> sideOnly(requiredSide: Side, op: () -> T) = object : ReadWriteProperty<Any, T> {
  val side = FMLCommonHandler.instance().side

  var value: T? = when (side) {
    requiredSide -> op()
    else -> null
  }

  override fun getValue(thisRef: Any, property: KProperty<*>) = (side == requiredSide)
    .iif { value!! }
    .ielse { error("Can't access this property (${property.name}) on $side side!") }

  override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = (side == requiredSide)
    .iif { this.value = value }
    .ielse { error("Can't access this property (${property.name}) on $side side!") }
}

fun <T : Any> clientOnly(op: () -> T) = sideOnly(Side.CLIENT, op)

fun <T : Any> serverOnly(op: () -> T) = sideOnly(Side.SERVER, op)