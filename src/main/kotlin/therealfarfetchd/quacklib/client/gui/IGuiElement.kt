package therealfarfetchd.quacklib.client.gui

/**
 * Created by marco on 16.07.17.
 */
interface IGuiElement {

  val width: Int
  val height: Int

  var elements: Set<GuiElement>

  fun render(mouseX: Int, mouseY: Int)

}