package therealfarfetchd.quacklib.api.block.component

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.data.BlockData
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.Applyable

private typealias Base = BlockComponent
private typealias TE = BlockComponentNeedTE
private typealias Reg = BlockComponentRegistered

interface BlockComponent : Applyable<BlockConfigurationScope>

interface BlockComponentNeedTE : Base

interface BlockComponentRegistered : Base {

  val rl: ResourceLocation

}

interface BlockComponentCapability : TE {

  fun <T> hasCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): Boolean =
    getCapability(data, capability, facing) != null

  fun <T> getCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): T?

}

interface BlockComponentActivation : Base {

  fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean

}

interface BlockComponentTickable : TE {

  fun onTick(data: BlockData)

}

interface BlockComponentDrops : Base {

  fun getDrops(data: BlockData): Set<ItemStack>

}

interface BlockComponentPickBlock : Base {

  fun getPickBlock(data: BlockData): ItemStack

}

interface BlockComponentData<T : BlockDataPart> : TE, Reg {

  var part: PartAccessToken<T>

  fun createPart(): T

  fun createPart(version: Int): BlockDataPart = error("Updating not implemented")

  fun update(version: Int, old: BlockDataPart, new: T): T = error("Updating not implemented")

  val BlockData.part
    get() = this@BlockComponentData.part.retrieve(this)

}

interface BlockComponentInfo : Base {

  fun getInfo(data: BlockData): List<String>

}