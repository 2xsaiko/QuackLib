package therealfarfetchd.quacklib.common

import net.minecraftforge.common.config.Configuration
import therealfarfetchd.quacklib.common.api.util.AutoLoad
import java.io.File

@AutoLoad
object QuackLibConfig : Configuration(File("config/quacklib.conf")) {

  var generateOres: Boolean = true

  init {
    generateOres = getBoolean("generate_ores", "world", true, "Allows for disabling ores generating in the world.")

    save()
  }

}