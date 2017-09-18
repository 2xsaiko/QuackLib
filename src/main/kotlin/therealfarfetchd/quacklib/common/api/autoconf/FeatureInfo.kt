package therealfarfetchd.quacklib.common.api.autoconf

class FeatureInfo(val feature: Feature) {
  var explicitEnabled = false
  var requiredByMods: Set<String> = emptySet()
  var requiredByFeatures: Set<Feature> = emptySet()
}