package therealfarfetchd.quacklib.common.api.autoconf

open class FeatureProperties {
  var depends: Set<Feature> = emptySet()
  var provides: Set<VirtualFeature> = emptySet()
  var conflicts: Set<Feature> = emptySet()
  var manualReg: Boolean = false
  var priority: Int = 0
  var action: () -> Unit= {}

  fun depends(vararg set: Feature) {
    depends += set
  }

  fun provides(vararg set: VirtualFeature) {
    provides += set
  }

  fun conflicts(vararg set: Feature) {
    conflicts += set
  }

  fun action(op: () -> Unit) {
    action = op
  }
}