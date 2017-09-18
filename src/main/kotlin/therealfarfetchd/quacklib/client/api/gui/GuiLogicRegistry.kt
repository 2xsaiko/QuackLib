package therealfarfetchd.quacklib.client.api.gui

import net.minecraft.util.ResourceLocation
import kotlin.reflect.KClass

object GuiLogicRegistry {

  private var registry: Map<ResourceLocation, () -> AbstractGuiLogic> = emptyMap()

  val fallback = ResourceLocation("quacklib:null_logic")

  fun register(rl: ResourceLocation, factory: () -> AbstractGuiLogic) {
    if (registry.containsKey(rl)) throw IllegalStateException("There is already a gui logic for $rl registered!")
    registry += rl to factory
  }

  fun register(name: String, factory: () -> AbstractGuiLogic) {
    register(ResourceLocation(name), factory)
  }

  fun <T : AbstractGuiLogic> register(rl: ResourceLocation, clazz: Class<T>) {
    clazz.newInstance() // immediately throw if this doesn't have an empty constructor
    register(rl, { clazz.newInstance() })
  }

  fun <T : AbstractGuiLogic> register(name: String, clazz: Class<T>) {
    register(ResourceLocation(name), clazz)
  }

  fun <T : AbstractGuiLogic> register(rl: ResourceLocation, clazz: KClass<T>) {
    register(rl, clazz.java)
  }

  fun <T : AbstractGuiLogic> register(name: String, clazz: KClass<T>) {
    register(ResourceLocation(name), clazz)
  }

  private fun constructOrNull(rl: ResourceLocation): AbstractGuiLogic? = registry[rl]?.invoke()

  fun construct(rl: ResourceLocation): AbstractGuiLogic {
    return constructOrNull(rl)
           ?: constructOrNull(fallback)
           ?: throw IllegalStateException("Fallback GUI logic ($fallback) not registered! Are we still initializing?")
  }

  fun construct(name: String): AbstractGuiLogic {
    return construct(ResourceLocation(name))
  }

}