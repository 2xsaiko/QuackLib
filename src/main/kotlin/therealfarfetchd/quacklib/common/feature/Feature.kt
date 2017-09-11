package therealfarfetchd.quacklib.common.feature

open class Feature(open val name: String, op: FeatureProperties.() -> Unit = {}) {
  open val dependsOn: Set<Feature>
  open val provides: Set<VirtualFeature>
  open val conflicts: Set<Feature>
  open val isInput: Boolean

  init {
    val fp = FeatureProperties()
    op(fp)
    provides = fp.provides
    conflicts = fp.conflicts
    dependsOn = fp.depends
    isInput = fp.manualOnly

    @Suppress("LeakingThis")
    FeatureManager.registerFeature(this)
  }

  open fun onActivate() {}
}

class ItemFeature(val meta: Int, name: String = "item $meta", op: FeaturePropertiesItem.() -> Unit = {}) : Feature(name) {
  override val dependsOn: Set<Feature>
  override val provides: Set<VirtualFeature>
  override val conflicts: Set<Feature>
  override val isInput: Boolean
  val oreDict: Set<String>

  init {
    val fp = FeaturePropertiesItem()
    op(fp)
    provides = fp.provides
    conflicts = fp.conflicts
    dependsOn = fp.depends + DefaultFeatures.ComponentItem
    isInput = fp.manualOnly
    oreDict = fp.oreDict
  }
}

class VirtualFeature(name: String) : Feature("[$name]")