package therealfarfetchd.quacklib.common.api.autoconf

class FeaturePropertiesItem : FeatureProperties() {
  var oreDict: Set<String> = emptySet()

  fun oreDict(vararg set: String) {
    oreDict += set
  }
}