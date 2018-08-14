@file:Suppress("NOTHING_TO_INLINE")

package therealfarfetchd.quacklib.render.model

import therealfarfetchd.quacklib.api.render.model.Model

inline fun Model.needsTESR() =
  needsDynamicRender() || needsGlRender()