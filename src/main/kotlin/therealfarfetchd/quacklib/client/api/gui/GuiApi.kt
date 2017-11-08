package therealfarfetchd.quacklib.client.api.gui

import com.google.gson.*
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Created by marco on 16.07.17.
 */

object GuiApi {
  val NameRegex = Regex("[a-z0-9_]*")

  private fun patchRL(rl: ResourceLocation): ResourceLocation = ResourceLocation(rl.resourceDomain, "gui/${rl.resourcePath}.json")

  fun loadGui(rl: ResourceLocation, logicParams: Map<String, Any?> = emptyMap()): QGuiScreen {
    val res = Minecraft.getMinecraft().resourceManager.getResource(patchRL(rl))
    val istr = res.inputStream ?: throw FileNotFoundException(rl.toString())
    val p = JsonParser()
    val tree = p.parse(InputStreamReader(istr))
    val logic = GuiLogicRegistry.construct(rl)
    val gui = QGuiScreen(logic)
    if (!tree.isJsonObject) throw GuiParserException()
    val jo = tree.asJsonObject
    populate(gui.root, jo.entrySet().filter { it.key !in setOf("elements") }.map { it.key to it.value }.toMap())
    if (jo.has("elements")) {
      parseElementsTag(gui.root, jo["elements"].asJsonArray)
    }
    logic.root = gui.root
    logic.params = logicParams
    logic.init()
    return gui
  }

  private fun parseElementsTag(parent: IGuiElement, a: JsonArray): List<GuiElement> {
    return a.map { parseElement(parent, it.asJsonObject) }
  }

  private fun parseElement(parent: IGuiElement, a: JsonObject): GuiElement {
    val elType = a["type"] ?: throw GuiParserException("type missing")
    val elName = a["name"]
    val type = elType.asString
    val el = GuiElementRegistry.construct(type)
    if (elName != null) {
      val name = elName.asString
      if (!name.matches(NameRegex)) throw GuiParserException("name must only contain lowercase characters, 0-9 and _")
      el.name = name
    }
    el.parent = parent
    parent.elements += el
    populate(el, a.entrySet().filter { it.key !in setOf("type", "name", "elements") }.map { it.key to it.value }.toMap())
    if (a.has("elements")) {
      parseElementsTag(el, a["elements"].asJsonArray)
    }
    return el
  }

  private fun populate(el: IGuiElement, props: Map<String, JsonElement>) {
    el.properties += props.map { it.key to javaRepr(it.value) }
  }

  private fun javaRepr(je: JsonElement): Any? {
    return when (je) {
      is JsonPrimitive -> handlePrimitive(je)
      is JsonObject -> handleObject(je)
      is JsonArray -> handleArray(je)
      else -> null
    }
  }

  private fun handlePrimitive(je: JsonPrimitive): Any {
    if (je.isBoolean) return je.asBoolean
    if (je.isString) return je.asString
    if (je.isNumber) return je.asBigDecimal
    throw IllegalStateException("What is this? $je (${je.javaClass})")
  }

  private fun handleArray(je: JsonArray): List<Any?> {
    return je.map { javaRepr(it) }
  }

  private fun handleObject(je: JsonObject): Map<String, Any?> {
    return je.entrySet().map { it.key to javaRepr(it.value) }.toMap()
  }
}