package therealfarfetchd.quacklib.common

import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.item.ItemStack
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.QuackLib

/**
 * Created by marco on 08.07.17.
 */
class TestQB : QBlock() {
  override val material: Material = Material.ROCK

  override fun getDroppedItems(): List<ItemStack> = listOf(QuackLib.tbitem.makeStack(1))

  override val properties: Set<IProperty<*>> = super.properties + PropBool

  override val collisionBox: AxisAlignedBB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0)

  override fun onAdded() {
    println("Welcome to $pos!")
  }

  companion object {
    val PropBool = PropertyBool.create("boolean")
  }
}