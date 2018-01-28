package therealfarfetchd.quacklib.common.block

import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import therealfarfetchd.quacklib.ModID
import therealfarfetchd.quacklib.common.api.extensions.getQBlock
import therealfarfetchd.quacklib.common.api.extensions.plus
import therealfarfetchd.quacklib.common.api.qblock.IQBlockMultiblock
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.qblock.WrapperImplManager
import therealfarfetchd.quacklib.common.api.util.BlockDef
import therealfarfetchd.quacklib.common.api.util.DataTarget
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

//@BlockDef(registerModels = false)
//class BlockMultiblockExtension : QBlock() {
//  private var rootOffset: BlockPos = BlockPos.ORIGIN
//
//  override fun onActivated(player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean {
//    return getMainBlock().onActivatedRemote(player, hand, facing, pos, hitX, hitY, hitZ)
//  }
//
//  private fun getMainBlock() = world.getQBlock(pos + rootOffset) as IQBlockMultiblock
//
//  override fun canStay(): Boolean {
//    val b = world.getQBlock(pos + rootOffset)
//    return b is IQBlockMultiblock && b.canStay()
//  }
//
//  override fun saveData(nbt: QNBTCompound, target: DataTarget) {
//    super.saveData(nbt, target)
//
//    nbt.ushort["xO"] = rootOffset.x
//    nbt.ushort["yO"] = rootOffset.y
//    nbt.ushort["zO"] = rootOffset.z
//  }
//
//  override fun loadData(nbt: QNBTCompound, target: DataTarget) {
//    super.loadData(nbt, target)
//
//    rootOffset = BlockPos(nbt.short["xO"].toInt(), nbt.short["yO"].toInt(), nbt.short["zO"].toInt())
//  }
//
//  override val material: Material = Material.IRON
//  override val blockType: ResourceLocation = ResourceLocation(ModID, "multiblock1")
//
//  override fun getItem(): ItemStack = ItemStack.EMPTY
//
//  companion object {
//    val Block by WrapperImplManager.container(BlockMultiblockExtension::class)
//  }
//}