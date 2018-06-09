package therealfarfetchd.quacklib.api.core.modinterface

import therealfarfetchd.quacklib.api.tools.ModContext

interface QuackLibAPI {

  val modContext: ModContext

  companion object {
    lateinit var impl: QuackLibAPI
  }
  
}