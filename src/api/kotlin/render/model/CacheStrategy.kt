package therealfarfetchd.quacklib.api.render.model

enum class CacheStrategy {
  /**
   * The model is identified by the properties that were queried during addObjects. This minimizes
   * addObjects calls, but can potentially slow down lookup times if a lot of variants are cached.
   * It's recommended to use Mixed instead of Partial.
   */
  Partial,

  /**
   * Same as Partial, except the full key is saved to the cache along with the partial key to allow
   * for faster lookups.
   */
  Mixed,

  /**
   * The model is identified by every property of the blockstate it was rendered with,
   * regardless if it was actually queried. Allows for slightly faster lookup times for models with
   * a lot of variants.
   */
  Full,

  /**
   * The model is recomputed every time it is asked for.
   * Not recommended.
   */
  DontCache,
}