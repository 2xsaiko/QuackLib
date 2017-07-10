package therealfarfetchd.quacklib.common.test

import mcmultipart.api.slot.EnumFaceSlot
import mcmultipart.api.slot.IPartSlot
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import therealfarfetchd.quacklib.QuackLib
import therealfarfetchd.quacklib.common.DataTarget
import therealfarfetchd.quacklib.common.extensions.isServer
import therealfarfetchd.quacklib.common.extensions.makeStack
import therealfarfetchd.quacklib.common.extensions.rotate
import therealfarfetchd.quacklib.common.qblock.IQBlockMultipart
import therealfarfetchd.quacklib.common.qblock.QBlock

/**
 * Created by marco on 08.07.17.
 */
internal class TestQB : QBlock(), IQBlockMultipart {

  private var boolToggle = false
  private var facing = EnumFacing.DOWN

  override val material: Material = Material.ROCK

  override fun getDroppedItems(): List<ItemStack> = listOf(QuackLib.tbitem.makeStack(1))

  override val properties: Set<IProperty<*>> = super.properties + PropBool + PropFacing

  override val collisionBox: AxisAlignedBB
    get() = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0).rotate(facing)

  override val isOpaque: Boolean = false
  override val isFullBlock: Boolean = false

  override fun onAdded() {
    println("Welcome to $pos!")
  }

  override fun onActivated(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
    if (world.isServer) {
      if (player.isSneaking) {
        dismantle()
      } else {
        boolToggle = !boolToggle
        dataChanged()
        clientDataChanged()
      }
    }
    return true
  }

  override fun saveData(nbt: NBTTagCompound, target: DataTarget) {
    nbt.setBoolean("toggle", boolToggle)
    nbt.setByte("facing", facing.index.toByte())
  }

  override fun loadData(nbt: NBTTagCompound, target: DataTarget) {
    boolToggle = nbt.getBoolean("toggle")
    facing = EnumFacing.getFront(nbt.getByte("facing").toInt())
  }

  override fun onPlaced(placer: EntityLivingBase?, stack: ItemStack?, sidePlaced: EnumFacing, hitX: Float, hitY: Float, hitZ: Float) {
    facing = sidePlaced
  }

  override fun applyProperties(state: IBlockState): IBlockState = state.withProperty(PropBool, boolToggle).withProperty(PropFacing, facing)

  override fun getPartSlot(): IPartSlot = EnumFaceSlot.fromFace(facing)

  override fun getPlacementSlot(facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, placer: EntityLivingBase?): IPartSlot {
    return EnumFaceSlot.fromFace(facing?.opposite ?: EnumFacing.DOWN)
  }

  internal companion object {
    val PropBool = PropertyBool.create("boolean")!!
    val PropFacing = PropertyEnum.create("facing", EnumFacing::class.java)
  }
}