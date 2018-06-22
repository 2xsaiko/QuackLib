package therealfarfetchd.quacklib.block.multipart.cbmp

import codechicken.multipart.MultiPartRegistry
import therealfarfetchd.quacklib.api.block.init.BlockConfiguration

object PartRegistrationHelper {

  fun registerBlock(def: BlockConfiguration) {
    MultiPartRegistry.registerParts({ _, _ -> MultipartQuackLib(def) }, setOf(def.rl))
  }

}