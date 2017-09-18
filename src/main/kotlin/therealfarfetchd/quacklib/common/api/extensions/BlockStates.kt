package therealfarfetchd.quacklib.common.api.extensions

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraftforge.common.property.IExtendedBlockState
import net.minecraftforge.common.property.IUnlistedProperty

/**
 * Created by marco on 08.07.17.
 */

operator fun <T : Comparable<T>, V : T> IBlockState.set(property: IProperty<T>, value: V): IBlockState = withProperty(property, value)!!

operator fun <T : Comparable<T>> IBlockState.get(property: IProperty<T>): T = getValue(property)

operator fun <V> IExtendedBlockState.set(property: IUnlistedProperty<V>, value: V): IExtendedBlockState = withProperty(property, value)!!

operator fun <V> IExtendedBlockState.get(property: IUnlistedProperty<V>): V = getValue(property)