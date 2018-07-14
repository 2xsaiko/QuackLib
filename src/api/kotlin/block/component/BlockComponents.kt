package therealfarfetchd.quacklib.api.block.component

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.block.data.BlockDataPart
import therealfarfetchd.quacklib.api.block.data.PartAccessToken
import therealfarfetchd.quacklib.api.block.init.BlockConfigurationScope
import therealfarfetchd.quacklib.api.block.multipart.PartSlot
import therealfarfetchd.quacklib.api.block.redstone.ConnectionMask
import therealfarfetchd.quacklib.api.core.init.Applyable
import therealfarfetchd.quacklib.api.objects.block.Block
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.world.World
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

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

  fun <T> hasCapability(block: Block, capability: Capability<T>, facing: Facing?): Boolean =
    getCapability(block, capability, facing) != null

  fun <T> getCapability(block: Block, capability: Capability<T>, facing: Facing?): T?

}

interface BlockComponentCapabilityMulti : TE {

  fun <T> hasCapability(block: Block, capability: Capability<T>, edge: ConnectionMask): Boolean =
    getCapability(block, capability, edge) != null

  fun <T> getCapability(block: Block, capability: Capability<T>, edge: ConnectionMask): T?

}

interface BlockComponentData<T : BlockDataPart> : TE, Reg {

  var part: PartAccessToken<T>

  fun createPart(): T

  fun createPart(version: Int): BlockDataPart =
    createPart().takeIf { it.version == version }
    ?: error("Updating not implemented")

  fun update(old: BlockDataPart, new: T): T =
    error("Updating not implemented")

  val Block.part
    get() = this[this@BlockComponentData.part]

}

interface BlockComponentDataExport<Self : BlockComponentDataExport<Self, D>, D : ExportedData<D, Self>> : Base {

  val exported: D

}

interface BlockComponentDataImport<Self : BlockComponentDataImport<Self, D>, D : ImportedData<D, Self>> : Base {

  val imported: D

}

interface BlockComponentPlacement<T : BlockDataPart> : BlockComponentData<T> {

  fun initialize(block: Block, placer: EntityLivingBase, hand: EnumHand, facing: Facing, hit: Vec3)

}

interface BlockComponentPlacementCheck : Base {

  fun canPlaceBlockAt(world: World, pos: PositionGrid, side: Facing?): Boolean

}

interface BlockComponentActivation : Base {

  fun onActivated(block: Block, player: EntityPlayer, hand: EnumHand, facing: Facing, hit: Vec3): Boolean

}

interface BlockComponentTickable : TE {

  fun onTick(block: Block)

}

interface BlockComponentDrops : Base {

  fun getDrops(block: Block): Set<Item>

}

interface BlockComponentPickBlock : Base {

  fun getPickBlock(block: Block): Item

}

interface BlockComponentInfo : Base {

  fun getInfo(block: Block): List<String>

}

interface BlockComponentCollision : Base {

  fun getCollisionBoundingBoxes(block: Block): List<AxisAlignedBB>

}

interface BlockComponentMouseOver : Base {

  fun getRaytraceBoundingBoxes(block: Block): List<AxisAlignedBB>

}

interface BlockComponentCustomMouseOver : Base {

  fun raytrace(block: Block, from: Vec3, to: Vec3): RayTraceResult

}

interface BlockComponentNeighborListener : Base {

  fun onNeighborChanged(block: Block, side: Facing)

}

interface BlockComponentMultipart : Base {

  fun getSlot(block: Block): PartSlot

  fun getExtraSlots(block: Block): List<PartSlot> = emptyList()

}

interface BlockComponentOcclusion : Base {

  fun getOcclusionBoundingBoxes(block: Block): List<AxisAlignedBB>

}

interface BlockComponentRedstone : Base {

  fun strongPowerLevel(block: Block, side: Facing): Int

  fun weakPowerLevel(block: Block, side: Facing): Int

  fun canConnectRedstone(block: Block, side: Facing): Boolean

}

interface BlockComponentRedstoneFace : BlockComponentRedstone {

  fun side(block: Block): Facing

}

interface BlockComponentRedstoneMask : BlockComponentRedstone {

  fun connectionMask(block: Block): Set<ConnectionMask>

}