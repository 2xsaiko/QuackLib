package therealfarfetchd.quacklib.block.component

import therealfarfetchd.quacklib.api.block.component.AppliedComponent
import therealfarfetchd.quacklib.api.block.component.BlockComponent

data class AppliedComponentImpl<T : BlockComponent>(override val instance: T) : AppliedComponent<T>