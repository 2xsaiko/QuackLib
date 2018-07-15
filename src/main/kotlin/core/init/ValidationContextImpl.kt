package therealfarfetchd.quacklib.core.init

import org.apache.logging.log4j.Level
import therealfarfetchd.quacklib.api.core.init.ValidationContext
import therealfarfetchd.quacklib.api.tools.Logger

class ValidationContextImpl(val thing: String) : ValidationContext {

  var messages: List<Message> = emptyList()

  var additionalInfo: String? = null

  private fun addMessage(msg: String, sev: Severity) {
    val realMsg =
      if (additionalInfo == null) msg
      else "[$additionalInfo] $msg"
    messages += Message(realMsg, sev)
  }

  override fun info(msg: String) {
    addMessage(msg, Severity.Info)
  }

  override fun warn(msg: String) {
    addMessage(msg, Severity.Warning)
  }

  override fun error(msg: String) {
    addMessage(msg, Severity.Error)
  }

  fun printMessages() {
    if (messages.isNotEmpty()) Logger.info("Messages for '$thing':")
    messages.forEach {
      val level = when (it.sev) {
        ValidationContextImpl.Severity.Info -> Level.INFO
        ValidationContextImpl.Severity.Warning -> Level.WARN
        ValidationContextImpl.Severity.Error -> Level.ERROR
      }
      Logger.log(level, it.text)
    }
    if (!isValid()) Logger.fatal("Errors exist for '$thing'")
  }

  fun isValid() = messages.none { it.sev == Severity.Error }

  data class Message(val text: String, val sev: Severity)

  enum class Severity { Info, Warning, Error }

}