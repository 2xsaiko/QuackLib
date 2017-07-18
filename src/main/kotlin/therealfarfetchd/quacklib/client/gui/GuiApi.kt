package therealfarfetchd.quacklib.client.gui

import com.google.gson.*
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Created by marco on 16.07.17.
 */

object GuiApi {
  private fun patchRL(rl: ResourceLocation): ResourceLocation = ResourceLocation(rl.resourceDomain, "gui/${rl.resourcePath}.json")

  fun loadGui(rl: ResourceLocation): QGuiScreen {
    val res = Minecraft.getMinecraft().resourceManager.getResource(patchRL(rl))
    val istr = res.inputStream ?: throw FileNotFoundException(rl.toString())
    val p = JsonParser()
    val tree = p.parse(InputStreamReader(istr))
    val gui = QGuiScreen()
    if (!tree.isJsonObject) throw GuiParserException()
    val jo = tree.asJsonObject
    populate(gui.root, jo.entrySet().filter { it.key !in setOf("elements") }.map { it.key to it.value }.toMap())
    if (jo.has("elements")) {
      parseElementsTag(jo["elements"].asJsonArray).forEach {
        gui.root.elements += it
        it.parent = gui.root
      }
    }
    return gui
  }

  internal fun parseElementsTag(a: JsonArray): List<GuiElement> {
    return a.map { parseElement(it.asJsonObject) }
  }

  internal fun parseElement(a: JsonObject): GuiElement {
    val elType = a["type"] ?: throw GuiParserException("type missing")
    val type = elType.asString
    val el = GuiElementRegistry.construct(type)
    populate(el, a.entrySet().filter { it.key !in setOf("type", "elements") }.map { it.key to it.value }.toMap())
    if (a.has("elements")) {
      parseElementsTag(a["elements"].asJsonArray).forEach {
        el.elements += it
        it.parent = el
      }
    }
    return el
  }

  internal fun populate(el: IGuiElement, props: Map<String, JsonElement>) {
    el.properties += props.map { it.key to javaRepr(it.value) }
  }

  internal fun javaRepr(je: JsonElement): Any? {
    when (je) {
      is JsonPrimitive -> return handlePrimitive(je)
      is JsonObject -> return handleObject(je)
      is JsonArray -> return handleArray(je)
      else -> return null
    }
  }

  internal fun handlePrimitive(je: JsonPrimitive): Any {
    if (je.isBoolean) return je.asBoolean
    if (je.isString) return je.asString
    if (je.isNumber) return je.asBigDecimal
    throw IllegalStateException("What is this? $je (${je.javaClass})")
  }

  internal fun handleArray(je: JsonArray): List<Any?> {
    return je.map { javaRepr(it) }
  }

  internal fun handleObject(je: JsonObject): Map<String, Any?> {
    return je.entrySet().map { it.key to javaRepr(it.value) }.toMap()
  }

}