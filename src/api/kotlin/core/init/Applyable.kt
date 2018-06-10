package therealfarfetchd.quacklib.api.core.init

interface Applyable<in T : Any> {

  fun onApplied(target: T) {}

  fun validate(target: T, vc: ValidationContext) {}

}