package therealfarfetchd.quacklib.common.autoconf

import net.minecraftforge.fml.common.Loader
import therealfarfetchd.quacklib.QuackLib

object FeatureManager {

  private var locked = false

  private var enabled: Map<Feature, FeatureInfo> = emptyMap()
  var available: Set<Feature> = emptySet(); private set

  val enabledFeatures: Set<Feature>
    get() = enabled.keys

  private var stack: List<Map<Feature, FeatureInfo>> = emptyList()

  fun depend(feature: Feature) {
    depend(feature, Loader.instance().activeModContainer()?.modId ?: "@unknown@")
  }

  fun depend(vararg feature: Feature) {
    feature.forEach(this::depend)
  }

  fun depend(feature: Feature, modid: String) {
    val di = enableFeature(feature)
    di.explicitEnabled = true
    di.requiredByMods += modid
  }

  fun depend(feature: String) {
    depend(lookupFeature(feature))
  }

  fun depend(feature: String, modid: String) {
    depend(lookupFeature(feature), modid)
  }

  fun dependSoft(feature: Feature): Boolean {
    return dependSoft(feature, Loader.instance().activeModContainer()?.modId ?: "@unknown@")
  }

  fun dependSoft(vararg feature: Feature): Boolean {
    return feature.all(this::dependSoft)
  }

  fun dependSoft(feature: Feature, modid: String): Boolean {
    push()
    return try {
      depend(feature, modid)
      drop()
      true
    } catch (e: IllegalStateException) {
      if (QuackLib.debug) QuackLib.Logger.warn(e.localizedMessage)
      pop()
      false
    }
  }

  fun dependSoft(feature: String): Boolean {
    return try {
      dependSoft(lookupFeature(feature))
    } catch (e: IllegalStateException) {
      false
    }
  }

  fun dependSoft(feature: String, modid: String): Boolean {
    return try {
      dependSoft(lookupFeature(feature), modid)
    } catch (e: IllegalStateException) {
      false
    }
  }

  fun push() {
    stack += enabled
  }

  fun pop() {
    enabled = stack.last()
    drop()
  }

  fun drop() {
    stack = stack.dropLast(1)
  }

  fun reset(condition: Boolean): Boolean {
    if (condition) drop() else pop()
    return condition
  }

  fun lookupFeature(feature: String): Feature {
    val matching = available.filter { it.name == feature }
    if (matching.isEmpty()) error("No feature $feature available!")
    if (matching.size > 1) error("Ambiguous feature: $feature!")
    return matching.first()
  }

  private fun requireAsDependency(parent: Feature, feature: Feature) {
    val di = enableFeature(feature)
    di.requiredByFeatures += parent
  }

  private fun enableFeature(feature: Feature): FeatureInfo {
    if (locked) {
      error("Tried to enable feature after feature manager was already locked! Please do this either in preinitialization or initialization.")
    }

    if (feature !in available) error("Unregistered feature '${feature.name}'")
    if (feature in enabled) return enabled.getValue(feature)
    val info = FeatureInfo(feature)
    enabled += feature to info

    feature.dependsOn.forEach { requireAsDependency(feature, it) }

    if (feature is VirtualFeature) {
      val provide = available.filter { it.provides.contains(feature) }
      val en = enabled.keys.filter { it.provides.contains(feature) }
      if (en.isNotEmpty()) {
        en.forEach { requireAsDependency(feature, it) }
      } else {
        val f = provide.maxBy { it.priority } ?: error("Nothing provides ${feature.name}!")
        requireAsDependency(feature, f)
      }
    }

    feature.onActivate()
    return info
  }

  fun isRequired(feature: Feature): Boolean = feature in enabled

  fun printFeatureList() {
    QuackLib.Logger.info("${available.size} features available, ${enabled.size} enabled.")
    if (enabled.isNotEmpty()) {
      QuackLib.Logger.info("=========================")
      available.forEach { f ->
        val i = enabled[f]
        var str = " ["
        str += if (i != null) "o" + if (i.explicitEnabled) "X" else " " else "  "
        str += "] '${f.name}'"
        if (i != null) {
          str += ", required by "
          str += (i.requiredByMods.map { "Mod($it)" } + i.requiredByFeatures.map { it.name }).joinToString()
        }
        QuackLib.Logger.info(str)
      }
      QuackLib.Logger.info("=========================")
    }
  }

  fun checkFeatures() {
    var errors: List<String> = emptyList()
    enabled.forEach { f, _ ->
      val ec = f.conflicts.filter { it in enabled.keys }
      if (ec.isNotEmpty()) {
        errors += "Enabled features ${ec.joinToString(prefix = "'", postfix = "'") { it.name }}' conflict with ${f.name}!"
      }
    }
    if (errors.isNotEmpty()) {
      errors.forEach(QuackLib.Logger::fatal)
      error("Errors occurred in feature manager. Cannot continue.")
    }
  }

  fun lockFeatures() {
    locked = true
  }

  fun registerFeature(feature: Feature) {
    available += feature
  }
}