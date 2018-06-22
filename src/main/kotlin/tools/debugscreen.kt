package therealfarfetchd.quacklib.tools

import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.util.text.TextFormatting

fun getTextState(list: MutableList<String>, state: IBlockState) {
  list.add("")
  list.add(Block.REGISTRY.getNameForObject(state.block).toString())

  fun <T : Comparable<T>> addToList(property: IProperty<T>, value: T) {
    var s = property.getName(value)

    when (value) {
      true -> s = TextFormatting.GREEN.toString() + s
      false -> s = TextFormatting.RED.toString() + s
    }

    list.add(property.name + ": " + s)
  }

  for ((property, t) in state.properties.entries) {
    @Suppress("UNCHECKED_CAST")
    addToList(property as IProperty<Comparable<Any>>, t as Comparable<Any>)
  }
}