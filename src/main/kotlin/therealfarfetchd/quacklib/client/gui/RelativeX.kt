package therealfarfetchd.quacklib.client.gui

/**
 * Created by marco on 16.07.17.
 */
enum class RelativeX {
  Left, Center, Right;

  companion object {
    fun byName(name: String): RelativeX = values().firstOrNull { it.name.toLowerCase() == name } ?: Left
  }
}