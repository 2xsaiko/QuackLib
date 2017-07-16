package therealfarfetchd.quacklib.client.gui

import net.minecraft.util.ResourceLocation
import kotlin.reflect.KClass

/**
 * Created by marco on 16.07.17.
 */
object GuiElementRegistry {

  private var registry: Map<ResourceLocation, () -> GuiElement> = emptyMap()

  val fallback = ResourceLocation("quacklib:dummy")

  fun register(rl: ResourceLocation, factory: () -> GuiElement) {
    if (registry.containsKey(rl)) throw IllegalStateException("There is already a gui element $rl registered!")
    registry += rl to factory
  }

  fun register(name: String, factory: () -> GuiElement) {
    register(ResourceLocation(name), factory)
  }

  fun <T : GuiElement> register(rl: ResourceLocation, clazz: Class<T>) {
    clazz.newInstance() // immediately throw if this doesn't have an empty constructor
    register(rl, { clazz.newInstance() })
  }

  fun <T : GuiElement> register(name: String, clazz: Class<T>) {
    register(ResourceLocation(name), clazz)
  }

  fun <T : GuiElement> register(rl: ResourceLocation, clazz: KClass<T>) {
    register(rl, clazz.java)
  }

  fun <T : GuiElement> register(name: String, clazz: KClass<T>) {
    register(ResourceLocation(name), clazz)
  }

  private fun constructOrNull(rl: ResourceLocation): GuiElement? = registry[rl]?.invoke()

  fun construct(rl: ResourceLocation): GuiElement {
    return constructOrNull(rl)
           ?: constructOrNull(fallback)
           ?: throw IllegalStateException("Fallback GUI element ($rl) not registered! Are we still initializing?")
  }

  fun construct(name: String): GuiElement {
    return construct(ResourceLocation(name))
  }

}