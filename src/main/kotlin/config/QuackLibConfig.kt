package therealfarfetchd.quacklib.config

import net.minecraftforge.common.config.Configuration
import java.io.File

object QuackLibConfig : Configuration(File("config/quacklib.conf")) {

  // TODO do stuff

  var alwaysShowMultipartDebug = true

  init {
    save()
  }

}