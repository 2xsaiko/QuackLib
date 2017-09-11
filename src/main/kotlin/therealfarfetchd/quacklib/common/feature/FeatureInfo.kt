package therealfarfetchd.quacklib.common.feature

class FeatureInfo(val feature: Feature) {
  var explicitEnabled = false
  var requiredByMods: List<String> = emptyList()
  var requiredByFeatures: List<Feature> = emptyList()
}