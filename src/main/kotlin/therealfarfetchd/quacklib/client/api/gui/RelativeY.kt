package therealfarfetchd.quacklib.client.api.gui

/**
 * Created by marco on 16.07.17.
 */
enum class RelativeY {
  Top, Center, Bottom;

  companion object {
    fun byName(name: String): RelativeY = values().firstOrNull { it.name.toLowerCase() == name } ?: Top
  }
}