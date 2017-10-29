package therealfarfetchd.quacklib.common.api.autoconf

import therealfarfetchd.quacklib.QuackLib

open class Feature(open val name: String, private val op: FeatureProperties.() -> Unit = {}) {
  protected var init: Boolean = false

  var dependsOn: Set<Feature> = emptySet()
    get() {
      if (!init) initProps()
      return field
    }

  var provides: Set<VirtualFeature> = emptySet()
    get() {
      if (!init) initProps()
      return field
    }

  var conflicts: Set<Feature> = emptySet()
    get() {
      if (!init) initProps()
      return field
    }

  var priority: Int = 0
    get() {
      if (!init) initProps()
      return field
    }

  init {
    val fp = FeatureProperties()
    op(fp)

    if (!fp.manualReg) {
      @Suppress("LeakingThis")
      FeatureManager.registerFeature(this)
    }
  }

  protected open fun initProps() {
    val fp = FeatureProperties()
    op(fp)
    provides = fp.provides
    conflicts = fp.conflicts
    dependsOn = fp.depends
    priority = fp.priority

    init = true
  }

  open fun onActivate() {
    if (QuackLib.debug)
      QuackLib.Logger.info("Enabled feature $name.")
    FeatureProperties().also(op).action[EnableAt.FeatureEnable]?.invoke()
  }

  open fun onGameInit() {
    FeatureProperties().also(op).action[EnableAt.GameInitEnd]?.invoke()
  }
}

class ItemFeature(val meta: Int, name: String = "item $meta", private val op: FeaturePropertiesItem.() -> Unit = {}) : Feature(name) {
  var oreDict: Set<String> = emptySet()
    get() {
      if (!init) initProps()
      return field
    }

  override fun initProps() {
    val fp = FeaturePropertiesItem()
    op(fp)
    provides = fp.provides
    conflicts = fp.conflicts
    dependsOn = fp.depends + DefaultFeatures.ComponentItem
    priority = fp.priority
    oreDict = fp.oreDict

    init = true
  }

  override fun onActivate() {
    super.onActivate()
    FeaturePropertiesItem().also(op).action[EnableAt.FeatureEnable]?.invoke()
  }

  override fun onGameInit() {
    super.onGameInit()
    FeaturePropertiesItem().also(op).action[EnableAt.GameInitEnd]?.invoke()
  }
}

class VirtualFeature(name: String) : Feature("[$name]")