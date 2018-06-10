package therealfarfetchd.quacklib.api.core.init

interface ValidationContext {

  fun info(msg: String)

  fun warn(msg: String)

  fun error(msg: String)

}