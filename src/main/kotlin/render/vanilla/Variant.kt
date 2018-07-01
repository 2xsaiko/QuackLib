package therealfarfetchd.quacklib.render.vanilla

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import therealfarfetchd.quacklib.api.core.init.ValidationContext

interface Variant {

  fun matches(state: IBlockState): Boolean

}

data class VariantFull(val state: IBlockState) : Variant {

  constructor(sc: BlockStateContainer, stringSpec: String, vc: ValidationContext) :
    this(VanillaLoader.parseBlockState(sc, stringSpec, vc))

  override fun matches(state: IBlockState): Boolean = state == this.state

}

data class VariantPart<T : Comparable<T>?>(val sc: BlockStateContainer, val property: IProperty<T>, val value: T) : Variant {

  override fun matches(state: IBlockState): Boolean {
    return state.block.blockState == sc && state.getValue(property) == value
  }

}