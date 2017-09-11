package therealfarfetchd.quacklib.common.feature

import net.minecraftforge.fml.common.Loader
import therealfarfetchd.quacklib.QuackLib

object FeatureManager {

  var enabled: Map<Feature, FeatureInfo> = emptyMap(); private set
  var available: Set<Feature> = emptySet(); private set

  init {
    DefaultFeatures
  }

  fun require(feature: Feature) {
    require(feature, Loader.instance().activeModContainer()?.modId ?: "@unknown@")
  }

  fun require(vararg feature: Feature) {
    feature.forEach(this::require)
  }

  fun require(feature: Feature, modid: String) {
    val di = enabled[feature] ?: FeatureInfo(feature).also { enabled += feature to it }
    di.explicitEnabled = true
    di.requiredByMods += modid
    feature.dependsOn.forEach { requireAsDependency(feature, it) }

    if (feature is VirtualFeature) {
      val provide = available.filterNot { it.isInput }.filter { it.provides.contains(feature) }
      val en = enabled.keys.filter { it.provides.contains(feature) }
      if (en.isNotEmpty()) {
        en.forEach { requireAsDependency(feature, it) }
      } else {
        val f = provide.firstOrNull() ?: error("Nothing provides ${feature.name}!")
        requireAsDependency(feature, f)
      }
    }
  }

  fun require(feature: String) {
    require(lookupFeature(feature))
  }

  fun require(feature: String, modid: String) {
    require(lookupFeature(feature), modid)
  }

  fun lookupFeature(feature: String): Feature {
    val matching = available.filter { it.name == feature }
    if (matching.isEmpty()) error("Nothing provides $feature!")
    if (matching.size > 1) error("Ambiguous feature: $feature!")
    return matching.first()
  }

  private fun requireAsDependency(parent: Feature, feature: Feature) {
    val di = enabled[feature] ?: FeatureInfo(feature).also {
      enabled += feature to it
      feature.dependsOn.forEach { requireAsDependency(feature, it) }
    }
    di.requiredByFeatures += parent
  }

  fun isRequired(feature: Feature): Boolean = feature in enabled

  fun printFeatureList() {
    QuackLib.Logger.info("${available.size} features available, ${enabled.size} enabled.")
    if (enabled.isNotEmpty()) {
      QuackLib.Logger.info("List of enabled features:")
      QuackLib.Logger.info("=========================")
      enabled.forEach { f, i ->
        var strs = listOf("[${if (i.explicitEnabled) 'e' else ' '}] '${f.name}':")
        if (i.requiredByFeatures.isNotEmpty()) strs += "     Required by: " + i.requiredByFeatures.joinToString { it.name }
        if (i.requiredByMods.isNotEmpty()) strs += "     Required by: " + i.requiredByMods.joinToString()
        strs.forEach(QuackLib.Logger::info)
      }
    }
  }

  fun checkFeatures() {
    var errors: List<String> = emptyList()
    enabled.forEach { f, i ->
      if (!i.explicitEnabled && f.isInput) {
        errors += "Feature '${f.name}' must be explicitly enabled!"
      }
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

  fun registerFeature(feature: Feature) {
    available += feature
  }
}