package therealfarfetchd.quacklib.common.feature

open class FeatureProperties {
  var depends: Set<Feature> = emptySet()
  var provides: Set<VirtualFeature> = emptySet()
  var conflicts: Set<Feature> = emptySet()
  var manualOnly: Boolean = false

  fun depends(vararg set: Feature) {
    depends += set
  }

  fun provides(vararg set: VirtualFeature) {
    provides += set
  }

  fun conflicts(vararg set: Feature) {
    conflicts += set
  }
}