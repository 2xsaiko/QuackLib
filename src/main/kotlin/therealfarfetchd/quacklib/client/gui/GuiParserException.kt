package therealfarfetchd.quacklib.client.gui

/**
 * Created by marco on 16.07.17.
 */
class GuiParserException(str: String) : RuntimeException(str) {
  constructor() : this("An unexpected error occurred while parsing a GUI definition!")
}