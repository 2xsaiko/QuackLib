package therealfarfetchd.quacklib.client.api.model

import com.google.common.collect.ImmutableMap
import net.minecraft.block.state.IBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty
import java.util.*

class ExtendedBlockStateWrapper(val state: IBlockState) : IBlockState by state, IExtendedBlockState {
  override fun <V : Any?> withProperty(property: IUnlistedProperty<V>?, value: V) =
    error("Can't have extended properties on this, it's just a wrapper!")

  override fun <V : Any?> getValue(property: IUnlistedProperty<V>?) =
    error("Can't have extended properties on this, it's just a wrapper!")

  override fun getUnlistedNames(): Collection<IUnlistedProperty<*>> = emptySet()

  override fun getUnlistedProperties(): ImmutableMap<IUnlistedProperty<*>, Optional<*>> = ImmutableMap.of()

  override fun getClean(): IBlockState = state
}

fun IBlockState.wrapIfNeeded(): IExtendedBlockState = when (this) {
  is IExtendedBlockState -> this
  else -> ExtendedBlockStateWrapper(this)
}