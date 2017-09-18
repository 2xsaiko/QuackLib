package therealfarfetchd.quacklib.client.api.gui

enum class ButtonType {
  Normal, Toggle;

  companion object {
    fun byName(name: String): ButtonType = values().firstOrNull { it.name.toLowerCase() == name } ?: Normal
  }
}