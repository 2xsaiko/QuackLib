package therealfarfetchd.quacklib.common.autoconf

class FeaturePropertiesItem : FeatureProperties() {
  var oreDict: Set<String> = emptySet()

  fun oreDict(vararg set: String) {
    oreDict += set
  }
}