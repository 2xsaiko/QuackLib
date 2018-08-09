package therealfarfetchd.quacklib.api.item.component

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import therealfarfetchd.math.Vec3
import therealfarfetchd.quacklib.api.core.init.Applyable
import therealfarfetchd.quacklib.api.item.Tool
import therealfarfetchd.quacklib.api.item.data.ItemDataPart
import therealfarfetchd.quacklib.api.item.data.PartAccessToken
import therealfarfetchd.quacklib.api.item.init.ItemConfigurationScope
import therealfarfetchd.quacklib.api.objects.item.Item
import therealfarfetchd.quacklib.api.objects.world.WorldMutable
import therealfarfetchd.quacklib.api.render.property.ComponentRenderProperties
import therealfarfetchd.quacklib.api.tools.Facing
import therealfarfetchd.quacklib.api.tools.PositionGrid

private typealias Base = ItemComponent

private typealias Reg = ItemComponentRegistered

interface ItemComponent : Applyable<ItemConfigurationScope>

interface ItemComponentRegistered : Base {

  val rl: ResourceLocation

}

interface ItemComponentRenderProperties : Base, Reg, ComponentRenderProperties

interface ItemComponentDataExport : Base

interface ItemComponentDataImport : Base

interface ItemComponentTool : Base {

  val toolTypes: Set<Tool>

}

interface ItemComponentCapability : Base {

  fun <T> hasCapability(item: Item, capability: Capability<T>, facing: Facing?): Boolean =
    getCapability(item, capability, facing) != null

  fun <T> getCapability(item: Item, capability: Capability<T>, facing: Facing?): T?

}

interface ItemComponentData<T : ItemDataPart> : Reg {

  var part: PartAccessToken<T>

  fun createPart(): T

  fun createPart(version: Int): ItemDataPart =
    createPart().takeIf { it.version == version }
    ?: error("Updating not implemented")

  fun update(old: ItemDataPart, new: T): T =
    error("Updating not implemented")

  val Item.part
    get() = this[this@ItemComponentData.part]

}

interface ItemComponentUse : Base {

  fun onUse(stack: Item, player: EntityPlayer, world: WorldMutable, pos: PositionGrid, hand: EnumHand, hitSide: Facing, hitVec: Vec3): EnumActionResult

}

interface ItemComponentTickable : Base {

  fun onTick(item: Item)

}

interface ItemComponentInfo : Base {

  fun getInfo(item: Item): List<String>

}
