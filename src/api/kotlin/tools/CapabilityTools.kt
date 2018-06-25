package therealfarfetchd.quacklib.api.tools

import therealfarfetchd.quacklib.api.core.modinterface.QuackLibAPI
import kotlin.reflect.KClass

inline fun <reified T : Any> registerCapability() = registerCapability(T::class)

fun <T : Any> registerCapability(type: KClass<T>) = QuackLibAPI.impl.registerCapability(type)

annotation class RegisterCapability