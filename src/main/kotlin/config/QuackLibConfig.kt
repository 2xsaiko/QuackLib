package therealfarfetchd.quacklib.config

import net.minecraftforge.common.config.Configuration
import java.io.File
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

object QuackLibConfig : Configuration(File("config/quacklib.conf")) {

  val alwaysShowMultipartDebug by storedBool(true, "misc", "Show real blockstate for non-QuackLib multiparts")

  init {
    // we read all config properties to initialize the values
    // can't use kotlin reflect because it makes the server crash. For some reason
    QuackLibConfig::class.java.declaredMethods.filter { it.parameterCount == 0 }.forEach { it.invoke(this) }

    save()
  }

  private fun storedBool(default: Boolean, category: String, comment: String) = storedT(default, category, comment, this::getBoolean)

  private fun <T> storedT(default: T, category: String, comment: String, read: (String, String, T, String, String) -> T) = object : ReadOnlyProperty<Configuration, T> {

    val r = Regex("([a-z]|[A-Z]+)([A-Z])")

    override fun getValue(thisRef: Configuration, property: KProperty<*>): T {
      val name = property.name.replace(r, "$1_$2").toLowerCase()
      val lang = "conf.quacklib.$category.$name"
      return read(name, category, default, comment, lang)
    }

  }

}