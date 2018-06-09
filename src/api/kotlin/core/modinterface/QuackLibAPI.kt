package therealfarfetchd.quacklib.api.core.modinterface

import therealfarfetchd.quacklib.api.tools.ModContext

interface QuackLibAPI {

  val modContext: ModContext

  val qlVersion: String

  companion object {
    lateinit var impl: QuackLibAPI
  }

}