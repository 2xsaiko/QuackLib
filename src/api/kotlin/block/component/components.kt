package therealfarfetchd.quacklib.api.block.component

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.data.BlockData
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.BlockDataRO
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.core.init.Applyable

private typealias Base = BlockComponent
private typealias TE = BlockComponentNeedTE
private typealias Reg = BlockComponentRegistered

interface BlockComponent : Applyable<BlockConfigurationScope>

interface BlockComponentNeedTE : Base

interface BlockComponentInternal : Base

interface BlockComponentRegistered : Base {

  val rl: ResourceLocation

}

interface BlockComponentCapability : TE {

  fun <T> hasCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): Boolean =
    getCapability(data, capability, facing) != null

  fun <T> getCapability(data: BlockData, capability: Capability<T>, facing: EnumFacing?): T?

}

interface BlockComponentData<T : BlockDataPart> : TE, Reg {

  var part: PartAccessToken<T>

  fun createPart(): T

  fun createPart(version: Int): BlockDataPart =
    createPart().takeIf { it.version == version }
    ?: error("Updating not implemented")

  fun update(old: BlockDataPart, new: T): T =
    error("Updating not implemented")

  val BlockDataRO.part
    get() = this@BlockComponentData.part.retrieve(this)

}

interface BlockComponentDataExport<Self : BlockComponentDataExport<Self, D>, D : ExportedData<D, Self>> : Base {

  val exported: D

}

interface BlockComponentDataImport<Self : BlockComponentDataImport<Self, D>, D : ImportedData<D, Self>> : Base {

  val imported: D

}

interface BlockComponentPlacement<T : BlockDataPart> : BlockComponentData<T> {

  fun initialize(world: IBlockAccess, pos: BlockPos, part: T, placer: EntityLivingBase, hand: EnumHand)

}

interface BlockComponentActivation : Base {

  fun onActivated(data: BlockData, player: EntityPlayer, hand: EnumHand, facing: EnumFacing, hit: Vec3): Boolean

}

interface BlockComponentTickable : TE {

  fun onTick(data: BlockData)

}

interface BlockComponentDrops : Base {

  fun getDrops(data: BlockDataRO): Set<ItemStack>

}

interface BlockComponentPickBlock : Base {

  fun getPickBlock(data: BlockDataRO): ItemStack

}

interface BlockComponentInfo : Base {

  fun getInfo(data: BlockDataRO): List<String>

}

interface BlockComponentCollision : Base {

  fun getCollisionBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB>

}

interface BlockComponentMouseOver : Base {

  fun getRaytraceBoundingBoxes(data: BlockDataRO): List<AxisAlignedBB>

}

interface BlockComponentCustomMouseOver : Base {

  fun raytrace(data: BlockDataRO, from: Vec3, to: Vec3): RayTraceResult

}