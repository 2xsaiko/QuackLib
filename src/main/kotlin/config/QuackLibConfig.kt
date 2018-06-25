package therealfarfetchd.quacklib.config

import net.minecraftforge.common.config.Configuration

object QuackLibConfig : Configuration() {

  // TODO do stuff

  var alwaysShowMultipartDebug = true

  init {
    save()
  }

}