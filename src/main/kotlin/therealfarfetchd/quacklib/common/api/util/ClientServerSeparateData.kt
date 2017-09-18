package therealfarfetchd.quacklib.common.api.util

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side
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